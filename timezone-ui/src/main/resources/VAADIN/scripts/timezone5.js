tday=new Array("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday");
tmonth=new Array("January","February","March","April","May","June","July","August","September","October","November","December");

function GetClock(){
	var elems = document.getElementsByClassName('clock');
	for (var i = 0, len = elems.length; i < len; i++){
		elems[i].innerHTML= getTime(elems[i].title)
	}
}

function getTime(offset){
	var curretnTime = new Date();
	var newOffset = Number(offset)+Number(curretnTime.getTimezoneOffset())
	var date=new Date(curretnTime.getTime() + (newOffset)*60000);

	var nday=date.getDay(),nmonth=date.getMonth(),ndate=date.getDate(),nyear=date.getYear();
	if(nyear<1000) nyear+=1900;
	var nhour=date.getHours(),nmin=date.getMinutes(),nsec=date.getSeconds(),ap;

	if(nhour==0){ap=" AM";nhour=12;}
	else if(nhour<12){ap=" AM";}
	else if(nhour==12){ap=" PM";}
	else if(nhour>12){ap=" PM";nhour-=12;}

	if(nmin<=9) nmin="0"+nmin;
	if(nsec<=9) nsec="0"+nsec;

	return ""+tday[nday]+", "+tmonth[nmonth]+" "+ndate+", "+nyear+" "+nhour+":"+nmin+":"+nsec+ap;
}

setInterval(GetClock,1000);