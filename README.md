# BlueChatV2

A real-time chat application built with Spring Boot, WebSocket, and Thymeleaf.

## Project Description

BlueChatV2 is a web-based chat application that allows users to create and join chat rooms for real-time communication. The application demonstrates the implementation of WebSocket for real-time messaging within a Spring Boot application. It features user authentication, room management, and instant messaging.

This project serves as a learning example for those interested in building real-time web applications using Java and Spring Boot.

## Features

- **User Authentication**: Secure login/logout functionality using Spring Security
- **Chat Rooms**: Create and join multiple chat rooms
- **Real-time Messaging**: Instant message delivery using WebSocket
- **User Presence**: See who is currently in a chat room
- **Join/Leave Notifications**: Automatic notifications when users join or leave rooms
- **Responsive Design**: Works on desktop and mobile devices
- **Session Management**: Proper handling of user sessions and disconnections

## Technology Stack

- **Backend**:
  - Java 17
  - Spring Boot 2.7.x
  - Spring WebSocket with STOMP
  - Spring Security
  - Lombok

- **Frontend**:
  - Thymeleaf template engine
  - Bootstrap 5
  - SockJS
  - STOMP.js
  - jQuery

## Setup Instructions

### Prerequisites

- JDK 17 or later
- Apache Maven 3.8+
- IDE (recommended: IntelliJ IDEA, Eclipse, or VS Code with Java extensions)

### Installation

1. Clone or download this repository:
   ```
   git clone https://github.com/yourusername/bluechat_v2.git
   cd bluechat_v2
   ```

2. Build the project using Maven:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

4. Access the application:
   Open a web browser and navigate to `http://localhost:8080`

## Usage Guide

1. **Login**: Use one of the default user credentials to log in.
2. **View Rooms**: After logging in, you'll see a list of available chat rooms.
3. **Create Room**: Click the "Create New Room" button to create a new chat room.
4. **Join Room**: Click the "Join Room" button on any room card to enter a chat room.
5. **Send Messages**: Type in the message input field and press "Send" or hit Enter.
6. **Leave Room**: Click the "Leave Room" button to exit the current chat room.
7. **Logout**: Use the dropdown menu in the navigation bar to log out.

## Development Environment Setup

1. Install JDK 17 from [Adoptium](https://adoptium.net/) or another provider.
2. Install Maven from [Apache Maven](https://maven.apache.org/download.cgi).
3. Add both Java and Maven to your system PATH.
4. Configure your IDE:
   - For IntelliJ IDEA, go to File > Settings > Build, Execution, Deployment > Build Tools > Maven and set the JDK.
   - For Eclipse, go to Window > Preferences > Java > Installed JREs and add your JDK.
5. Import the project as a Maven project in your IDE.
6. Enable annotation processing in your IDE to support Lombok.

## Default User Credentials

| Username | Password | Role       |
|----------|----------|------------|
| user1    | password | USER       |
| user2    | password | USER       |
| admin    | admin    | ADMIN, USER|

These credentials are defined in the `SecurityConfig.java` file and are for demonstration purposes only. In a production environment, you would use a database-backed authentication system.

## Contributing Guidelines

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/your-feature-name`).
3. Make your changes.
4. Run tests and ensure the application works correctly.
5. Commit your changes (`git commit -m 'Add some feature'`).
6. Push to the branch (`git push origin feature/your-feature-name`).
7. Create a new Pull Request.

## License

This project is open-source and available under the MIT License.

## Acknowledgments

- Spring Boot team for the amazing framework
- Bootstrap team for the responsive UI components
- All contributors who help improve this project

