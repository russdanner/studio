Changes made to tiny_mce.js:

#1) Decrease the RTE's absolute minimum height

This snippet of code:
if(G.test(""+A)){A=Math.max(parseInt(A)+(q.deltaHeight||0),74)}

was originally:
if(G.test(""+A)){A=Math.max(parseInt(A)+(q.deltaHeight||0),100)}


#2) Removed setting remove_trailing_brs option to true

This snippet of code:
a.dom.Serializer=function(e,i,f){var h,b,d=a.isIE,g=a.each,c;if(!e.apply_source_formatting){e.indent=false}

Was originally:
a.dom.Serializer=function(e,i,f){var h,b,d=a.isIE,g=a.each,c;if(!e.apply_source_formatting){e.indent=false}e.remove_trailing_brs=true;
