import { ChatAdapter, IChatGroupAdapter, User, Group} from 'ng-chat';
import { Message, ChatParticipantStatus, ParticipantResponse, ParticipantMetadata, ChatParticipantType, IChatParticipant } from 'ng-chat';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import {ViewChild} from '@angular/core';

export class ChatBoxAdapter extends ChatAdapter implements IChatGroupAdapter {
  public static mockedParticipants: IChatParticipant[] = [
    {
      participantType: ChatParticipantType.User,
      id: 1,
      displayName: 'Assistant',
      avatar: 'https://66.media.tumblr.com/avatar_9dd9bb497b75_128.pnj',
      status: ChatParticipantStatus.Online
    }];

  listFriends(): Observable<ParticipantResponse[]> {
    return of(ChatBoxAdapter.mockedParticipants.map(user => {
      const participantResponse = new ParticipantResponse();

      participantResponse.participant = user;
      participantResponse.metadata = {
        totalUnreadMessages: Math.floor(Math.random() * 10)
      }

      return participantResponse;
    }));
  }

  getMessageHistory(destinataryId: any): Observable<Message[]> {
    let mockedHistory: Array<Message>;

    mockedHistory = [
      {
        fromId: 1,
        toId: 999,
        message: 'Hello, I am your data assistant. How can I help you?',
        dateSent: new Date()
      }
    ];

    return of(mockedHistory).pipe(delay(2000));
  }

  sendMessage(message: Message): void {
    setTimeout(() => {
      const replyMessage = new Message();

      replyMessage.message = 'You have typed \'' + message.message + '\'';
      replyMessage.dateSent = new Date();

      if (isNaN(message.toId)) {
        const group = ChatBoxAdapter.mockedParticipants.find(x => x.id === message.toId) as Group;

        // Message to a group. Pick up any participant for this
        const randomParticipantIndex = Math.floor(Math.random() * group.chattingTo.length);
        replyMessage.fromId = group.chattingTo[randomParticipantIndex].id;

        replyMessage.toId = message.toId;

        this.onMessageReceived(group, replyMessage);
      } else {
        replyMessage.fromId = message.toId;
        replyMessage.toId = message.fromId;

        const user = ChatBoxAdapter.mockedParticipants.find(x => x.id === replyMessage.fromId);

        this.onMessageReceived(user, replyMessage);
      }
    }, 1000);
  }

  groupCreated(group: Group): void {
  }
}
