import { TestBed, async } from '@angular/core/testing';
import { AppComponent } from './app.component';
import {SidebarComponent} from './components/sidebar/sidebar.component';
import {ChatboxComponent} from './components/chatbox/chatbox.component';
import {DataViewContainerComponent} from './components/data-view-container/data-view-container.component';
import {IntroComponent} from './components/intro/intro.component';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        SidebarComponent,
        ChatboxComponent,
        DataViewContainerComponent,
        IntroComponent,
      ],
    }).compileComponents();
  }));
  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));
  it(`should have as title 'app'`, async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.title).toEqual('app');
  }));
  it('should render title in a h1 tag', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain('Welcome to app!');
  }));
});
