export class Message {
  content: string;
  timestamp: Date;
  avatar: string;
  username: string;
  constructor(content: string, username: string , avatar: string, timestamp?: Date) {
    this.content = content;
    this.timestamp = timestamp;
    this.avatar = avatar;
    this.username = username;
  }
}
