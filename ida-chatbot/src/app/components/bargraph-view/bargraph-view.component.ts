import {Component, Input, OnInit} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

@Component({
  selector: 'app-bargraph-view',
  templateUrl: './bargraph-view.component.html',
  styleUrls: ['./bargraph-view.component.css']
})
export class BargraphViewComponent implements OnInit {
  @Input('content')
  public content: any;
  constructor(public uip: UniqueIdProviderService) { }

  ngOnInit() {
  }

}
