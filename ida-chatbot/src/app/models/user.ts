export class User {
  id: number
  firstname: string;
  lastname: string;
  username: string;
  constructor(id: number, firstname: string, lastname: string, username: string) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.username = username;
  }
}
