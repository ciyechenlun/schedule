function setDivHeight(sideleft,sideright){  var   a=document.getElementById(sideleft);  var   b=document.getElementById(sideright);  if(a==null || b==null) {return;  }  if(a.clientHeight<b.clientHeight)     {     a.style.height=b.clientHeight+"px";     }     else{     b.style.height=a.clientHeight+"px";     }   }