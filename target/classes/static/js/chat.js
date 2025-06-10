/**
 * Chat application JavaScript
 * Handles WebSocket connections, message sending/receiving, and UI updates
 */

// Global variables
let stompClient = null;
let username = null;
let roomId = null;
let roomName = null;
let participants = new Set();
let reconnectCount = 0;
const MAX_RECONNECT_ATTEMPTS = 5;
const RECONNECT_DELAY = 3000; // 3 seconds

/**
 * Initialize the chat application
 * @param {string} currentUsername - The logged-in user's username
 * @param {string} currentRoomId - The ID of the current chat room
 * @param {string} currentRoomName - The name of the current chat room
 */
function initChat(currentUsername, currentRoomId, currentRoomName) {
    username = currentUsername;
    roomId = currentRoomId;
    roomName = currentRoomName;
    
    // Update room title
    document.getElementById('roomTitle').textContent = roomName;
    
    // Add current user to participants list
    addParticipant(username, true);
    
    // Connect to WebSocket server
    connect();
    
    // Set up message form submission
    document.getElementById('messageForm').addEventListener('submit', sendMessage);
    
    // Focus on message input
    document.getElementById('message').focus();
    
    // Set up window unload handler
    window.addEventListener('beforeunload', disconnect);
}

/**
 * Connect to the WebSocket server
 */
function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    // Disable console logging from STOMP
    stompClient.debug = null;
    
    stompClient.connect({}, 
        function(frame) {
            console.log('Connected to WebSocket: ' + frame);
            reconnectCount = 0;
            
            // Subscribe to room messages
            stompClient.subscribe('/topic/room/' + roomId, onMessageReceived);
            
            // Send join notification
            sendJoinMessage();
            
            // Show connected status
            showConnectionStatus(true);
            
            // Enable send button
            document.getElementById('sendButton').disabled = false;
        },
        function(error) {
            console.error('WebSocket connection error: ', error);
            showConnectionStatus(false);
            
            // Disable send button
            document.getElementById('sendButton').disabled = true;
            
            // Attempt reconnection
            if (reconnectCount < MAX_RECONNECT_ATTEMPTS) {
                reconnectCount++;
                console.log(`Reconnecting in ${RECONNECT_DELAY/1000} seconds... (Attempt ${reconnectCount}/${MAX_RECONNECT_ATTEMPTS})`);
                setTimeout(connect, RECONNECT_DELAY);
            } else {
                console.error('Maximum reconnection attempts reached.');
                alert('Could not connect to the chat server after several attempts. Please refresh the page to try again.');
            }
        }
    );
}

/**
 * Disconnect from WebSocket server
 */
function disconnect() {
    if (stompClient !== null && stompClient.connected) {
        // Send leave message before disconnecting
        sendLeaveMessage();
        
        // Disconnect after a small delay to ensure leave message is sent
        setTimeout(function() {
            stompClient.disconnect();
            console.log("Disconnected from WebSocket");
        }, 300);
    }
}

/**
 * Send a join notification to the server
 */
function sendJoinMessage() {
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/chat.addUser/" + roomId, {}, 
            JSON.stringify({
                type: 'JOIN',
                content: username + ' joined the chat room',
                sender: username,
                roomId: roomId
            })
        );
    }
}

/**
 * Send a leave notification to the server
 */
function sendLeaveMessage() {
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/chat.sendMessage/" + roomId, {}, 
            JSON.stringify({
                type: 'LEAVE',
                content: username + ' left the chat room',
                sender: username,
                roomId: roomId
            })
        );
    }
}

/**
 * Send a chat message
 * @param {Event} event - Form submission event
 */
function sendMessage(event) {
    event.preventDefault();
    
    const messageInput = document.getElementById('message');
    const messageContent = messageInput.value.trim();
    
    if (messageContent && stompClient && stompClient.connected) {
        const chatMessage = {
            type: 'CHAT',
            content: messageContent,
            sender: username,
            roomId: roomId
        };
        
        stompClient.send("/app/chat.sendMessage/" + roomId, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    
    // Focus back on input
    messageInput.focus();
}

/**
 * Handle received messages
 * @param {Object} payload - Message payload from STOMP
 */
function onMessageReceived(payload) {
    console.log('Received message payload:', payload.body);
    const message = JSON.parse(payload.body);
    console.log('Parsed message:', message);
    const messageArea = document.getElementById('messageArea');
    
    // Create message element
    const messageElement = document.createElement('div');
    messageElement.classList.add('message');
    
    // Create message bubble
    const bubbleElement = document.createElement('div');
    bubbleElement.classList.add('message-bubble');
    
    // Style based on message type
    if (message.type === 'JOIN') {
        messageElement.classList.add('message-system');
        
        // Add participant to the list
        addParticipant(message.sender);
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('message-system');
        
        // Remove participant from the list
        removeParticipant(message.sender);
    } else {
        if (message.sender === username) {
            messageElement.classList.add('message-self');
        } else {
            messageElement.classList.add('message-other');
            
            // Add sender name for other users
            const senderElement = document.createElement('div');
            senderElement.classList.add('sender');
            senderElement.textContent = message.sender;
            bubbleElement.appendChild(senderElement);
            
            // Add participant to the list if not already there
            addParticipant(message.sender);
        }
    }
    
    // Add message text
    const textElement = document.createElement('div');
    textElement.classList.add('message-content');
    textElement.textContent = message.content;
    bubbleElement.appendChild(textElement);
    
    // Add timestamp
    const timeElement = document.createElement('div');
    timeElement.classList.add('time');
    let messageTime;
    if (message.timestamp) {
        // Handle LocalDateTime format from Java (array format)
        if (Array.isArray(message.timestamp)) {
            // LocalDateTime is serialized as [year, month, day, hour, minute, second, nano]
            const [year, month, day, hour, minute, second] = message.timestamp;
            messageTime = new Date(year, month - 1, day, hour, minute, second); // month is 0-indexed in JS
        } else {
            // Try to parse as ISO string
            messageTime = new Date(message.timestamp);
        }
    } else {
        messageTime = new Date();
    }
    timeElement.textContent = formatTime(messageTime);
    bubbleElement.appendChild(timeElement);
    
    // Append bubble to message
    messageElement.appendChild(bubbleElement);
    
    // Add to message area
    messageArea.appendChild(messageElement);
    
    // Scroll to bottom
    scrollToBottom();
}

/**
 * Add a participant to the list
 * @param {string} participant - Username to add
 * @param {boolean} isCurrentUser - Whether this is the current user
 */
function addParticipant(participant, isCurrentUser = false) {
    if (!participants.has(participant)) {
        participants.add(participant);
        updateParticipantsList();
    }
}

/**
 * Remove a participant from the list
 * @param {string} participant - Username to remove
 */
function removeParticipant(participant) {
    if (participants.has(participant)) {
        participants.delete(participant);
        updateParticipantsList();
    }
}

/**
 * Update the participants list in the UI
 */
function updateParticipantsList() {
    const participantsList = document.getElementById('participantsList');
    participantsList.innerHTML = '';
    
    let sortedParticipants = Array.from(participants).sort();
    
    // Ensure current user is at the top
    if (participants.has(username)) {
        sortedParticipants = sortedParticipants.filter(p => p !== username);
        sortedParticipants.unshift(username);
    }
    
    sortedParticipants.forEach(participant => {
        const participantElement = document.createElement('div');
        participantElement.classList.add('participant');
        
        if (participant === username) {
            participantElement.classList.add('active');
            participantElement.innerHTML = `<strong>${participant}</strong> (You)`;
        } else {
            participantElement.textContent = participant;
        }
        
        participantsList.appendChild(participantElement);
    });
    
    // Update count
    document.getElementById('participantCount').textContent = participants.size;
}

/**
 * Format a date as a time string
 * @param {Date} date - Date to format
 * @returns {string} Formatted time string
 */
function formatTime(date) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

/**
 * Scroll the message area to the bottom
 */
function scrollToBottom() {
    const messageArea = document.getElementById('messageArea');
    messageArea.scrollTop = messageArea.scrollHeight;
}

/**
 * Show connection status
 * @param {boolean} connected - Whether connected or not
 */
function showConnectionStatus(connected) {
    const statusElement = document.getElementById('connectionStatus');
    if (statusElement) {
        if (connected) {
            statusElement.textContent = 'Connected';
            statusElement.className = 'badge bg-success';
        } else {
            statusElement.textContent = 'Disconnected';
            statusElement.className = 'badge bg-danger';
        }
    }
}

// Auto-initialization when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    const usernameElement = document.getElementById('username');
    const roomIdElement = document.getElementById('roomId');
    const roomNameElement = document.getElementById('roomName');
    
    if (usernameElement && roomIdElement && roomNameElement) {
        const currentUsername = usernameElement.value;
        const currentRoomId = roomIdElement.value;
        const currentRoomName = roomNameElement.value;
        
        if (currentUsername && currentRoomId && currentRoomName) {
            initChat(currentUsername, currentRoomId, currentRoomName);
        } else {
            console.error('Missing required chat initialization parameters');
        }
    } else {
        console.error('Chat initialization elements not found');
    }
});

