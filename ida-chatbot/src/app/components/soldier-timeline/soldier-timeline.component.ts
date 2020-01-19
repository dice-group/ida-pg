import { Component, OnInit, Input } from '@angular/core'

@Component({
  selector: 'app-soldier-timeline',
  templateUrl: './soldier-timeline.component.html',
  styleUrls: ['./soldier-timeline.component.css']
})
export class SoldierTimelineComponent implements OnInit {

  @Input('data') soldierData: any;
  allDates = []
  allDOB = []
  allDOD = []
  allDatesOfRank = []
  allDatesOfRegiment = []
  allDatesOfDecoration = []
  temp =''
  
  public dateCorectValue: Map<String, String> = new Map<String, String>()
  public soldierDatesVsFileds: Map<String, Map<String, String>> = new Map<String, Map<String, String>>()
  
  constructor() { }

  ngOnInit() {
  
    for (const key in this.soldierData.dates) {
      this.allDates.push(key)
    }
    
    for (const key in this.soldierData.correctDates) {
      this.dateCorectValue.set(key,this.soldierData.correctDates[key])
    }

    for (const key in this.soldierData.allDOB) {
      this.allDOB.push(key)
    }

    for (const key in this.soldierData.allRegiment) {
      this.allDatesOfRegiment.push(key)
    }

    for (const key in this.soldierData.allRanks) {
      this.allDatesOfRank.push(key)
    }

    for (const key in this.soldierData.allDecoration) {
      this.allDatesOfDecoration.push(key)
    }

    for (const key in this.soldierData.dates) {

      this.soldierDatesVsFileds.set(key,new Map<String, String>())
      this.temp = this.soldierData.dates[key]

      for(const key123 in this.soldierData[this.temp])
      {      
        this.soldierDatesVsFileds.get(key).set(key123,this.soldierData[this.temp][key123])
      }
    }
  }

}
