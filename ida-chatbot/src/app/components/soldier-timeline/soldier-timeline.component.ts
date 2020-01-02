import { Component, OnInit, Input } from '@angular/core'

@Component({
  selector: 'app-soldier-timeline',
  templateUrl: './soldier-timeline.component.html',
  styleUrls: ['./soldier-timeline.component.css']
})
export class SoldierTimelineComponent implements OnInit {

  @Input("data") soldierData: any;
  allDates = [];
  allDOB = [];
  allDOD = [];
  allDatesOfRank = [];
  allDatesOfRegiment = [];
  allDatesOfDecoration = [];

  public soldierInfo: Map<String, String> = new Map<String, String>();
  public soldierDOBInfo: Map<String, Map<String, String>> = new Map<String, Map<String, String>>();
  public soldierRegiementInfo: Map<String, Map<String, String>> = new Map<String, Map<String, String>>();
  public dateCorectValue: Map<String, String> = new Map<String, String>();
  public soldierDatesVsFileds: Map<String, Map<String, String>> = new Map<String, Map<String, String>>();
  
  soldierDOBdate = "";
  soldierDOBPlace = "";
  sInfo: any;
  //soldierDOBInfo : any;

  soldierDODdate = "";
  soldierDODPlace = "";
  temp = "";

  constructor() { }

  ngOnInit() {
    this.sInfo = {
      "firstName": this.soldierData.basicInfo__0.firstName || ""
    };
    
    for (const key in this.soldierData.dates) {
      this.allDates.push(key);
    }
    
    for (const key in this.soldierData.correctDates) {
      this.dateCorectValue.set(key,this.soldierData.correctDates[key]);
    }

    for (const key in this.soldierData.allDOB) {
      this.allDOB.push(key);
    }

    for (const key in this.soldierData.allRegiment) {
      this.allDatesOfRegiment.push(key);
    }

    for (const key in this.soldierData.allRanks) {
      this.allDatesOfRank.push(key);
    }

    for (const key in this.soldierData.allDecoration) {
      this.allDatesOfDecoration.push(key);
    }

    for (const key in this.soldierData.dates) {
      //this.dateCorectValue.set(key,this.soldierData.correctDates[key]);
      console.log("key = "+ key +"  value ="+this.soldierData.dates[key]);
      this.soldierDatesVsFileds.set(key,new Map<String, String>());
      this.temp = this.soldierData.dates[key];

      //console.log("this.temp = "+ this.temp + "   this.soldierData[this.temp]= "+this.soldierData[this.temp]);

      for(const key123 in this.soldierData[this.temp])
      {
        console.log("key1 = "+ key123 +"  value ="+this.soldierData[this.temp][key123]);
      
        this.soldierDatesVsFileds.get(key).set(key123,this.soldierData[this.temp][key123]);
      }
    }


/*    var datesFlag = 1;

    this.soldierDOBInfo.set("11/10/1994" + "_" + datesFlag, new Map<String, String>());
    this.soldierDOBInfo.get("11/10/1994" + "_" + datesFlag).set("date", "");
    this.soldierDOBInfo.get("11/10/1994" + "_" + datesFlag).set("place", "abc");
    this.dateCorectValue.set("11/10/1994" + "_" + datesFlag, "11/10/1994");
    this.allDates.push("11/10/1994" + "_" + datesFlag);
    this.allDOB.push("11/10/1994" + "_" + datesFlag);
    datesFlag += 1;

    this.soldierDOBInfo.set("11/10/1994" + "_" + datesFlag, new Map<String, String>());
    this.soldierDOBInfo.get("11/10/1994" + "_" + datesFlag).set("date", "");
    this.soldierDOBInfo.get("11/10/1994" + "_" + datesFlag).set("place", "xyz");
    this.dateCorectValue.set("11/10/1994" + "_" + datesFlag, "11/10/1994");
    this.allDates.push("11/10/1994" + "_" + datesFlag);
    this.allDOB.push("11/10/1994" + "_" + datesFlag);
    datesFlag += 1;

    this.dateCorectValue.set("20/12/2040", "20/12/2040");
    this.dateCorectValue.set("20/12/2042", "20/12/2042");
    this.dateCorectValue.set("20/12/2080", "20/12/2080");


    this.soldierRegiementInfo.set("20/12/2042", new Map<String, String>());
    this.soldierRegiementInfo.get("20/12/2042").set("applicableFrom_inXSDDate", "20/12/2042");
    
    this.allDates.sort();*/

  }

}
