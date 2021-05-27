const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

const database = admin.database()
const messaging = admin.messaging()

 exports.sendNotification = functions.database.ref('/Messages/{chatId}/{msgId}')
 .onCreate((snapshot,context) =>{
    const chat_id = context.params.chatId;
     const messageFrom = snapshot.val().senderId;
     const messageText = snapshot.val().msg;
     const user_id = chat_id.replace(messageFrom,'');
     console.log(chat_id+messageFrom+user_id);

     const cond = database.ref('/Chats/'+user_id+'/'+messageFrom).once("value",function(snapshot){
        const chatOpenValue = snapshot.val().chatOpen;
        console.log("chatOpen value",chatOpenValue);
         if(chatOpenValue !== true){
           const status = database.ref('/Users/'+messageFrom+'/userName').once("value",function(snapshot){
                            const senderName = snapshot.val()
                            const statusSecond =  database.ref('/Users/'+user_id).once("value",function(snapshot){
                                    console.log(snapshot.val());
                                    const targetToken = snapshot.val().deviceToken;
                                    var message = {
                                        notification : {
                                            title:senderName,
                                            body:""
                                        },
                                        token:targetToken,
                                        data : {
                                            sentText : messageText
                                        }
                                    }
                                    messaging.send(message).then((response)=>{
                                        console.log("Message Sent Successfully",response)
                                        return response
                                    }).catch((error)=>{
                                        console.log("Error Sending message",error)
                                    });
                                    console.log(targetToken);
                                    return true;
                                 })
                                 return true;
                     })
             }
        return chatOpenValue;
     })
     console.log("cond",cond);






     return cond;
 })
