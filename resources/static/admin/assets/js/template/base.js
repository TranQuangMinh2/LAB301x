//var console=(function(oldCons){
//    return {
//        log: function(text){
//            oldCons.log("%cTOPSHOE: %c" + text, 
//            			"font-size: 17px; color: #2897fe; font-weight: bold;", 
//            			"font-size: 12.7px; color: #ff6600");
//        },
//        info: function (text) {
//            oldCons.info(text);
//        },
//        warn: function (text) {
//            oldCons.warn(text);
//        },
//        error: function (text) {
//            oldCons.error(text);
//        }
//    };
//}(window.console));

//window.console = console;

var topshoe=(function(){
    return {
        log: function(text){
        	console.log("%cTOPSHOE: %c" + text, 
            			"font-size: 17px; color: #2897fe; font-weight: bold;", 
            			"font-size: 12.7px; color: #ff6600");
        },
        info: function (text) {
        	console.info(text);
        },
        warn: function (text) {
        	console.warn(text);
        },
        error: function (text) {
        	console.error(text);
        }
    };
}(window.console));
