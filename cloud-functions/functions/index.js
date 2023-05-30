const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

const DB = admin.firestore();
const DEAL_COLLECTION = "Deal";
const OPINION_COLLECTION = "Opinion";
const CREATION_COLLECTION = "Creation";
const ALERTE_COLLECTION = "Alerte";

exports.deleteExpiredDeals = functions
    .runWith({maxInstances: 5})
    .pubsub
    .schedule("every 24 hours")
    .onRun(async (context) => {
      const today = new Date();
      const snapshot = await DB.collection(DEAL_COLLECTION).get();
      snapshot.forEach(async (doc) => {
        const date = doc.data().expiration;
        const dateParts = date.split("/");
        const dateFormat = new Date(
            dateParts[2],
            dateParts[1] - 1,
            dateParts[0]);
        console.log(date);
        if (dateFormat < today) {
          await DB.collection(DEAL_COLLECTION).doc(doc.id).delete();
        }
      });
    });

exports.sendNotificationOnDealLike = functions
    .runWith({maxInstances: 5})
    .firestore
    .document("Opinion/{opinionId}")
    .onCreate(async (snap, context) => {
      const opinion = snap.data();
      if (opinion.isLike === true) {
        const dealRef = opinion.dealRef;
        const dealSnap = await dealRef.get();
        const deal = dealSnap.data();
        const opinionsSnap = await DB.collection(OPINION_COLLECTION)
            .where("dealRef", "==", dealRef)
            .where("isLike", "==", true)
            .get();
        const likes = opinionsSnap.size;
        let title;
        if (likes === 1) {
          title = "Un bon début !!!";
        } else if (likes === 10) {
          title = "Ahhh c'était pas un coup de chance !!!";
        } else if (likes === 100) {
          title = "Wow là c'est une dinguerie !!!";
        }
        if (title) {
          const body = `Votre deal "${deal.name}" a été aimé pour la ${likes}ème fois !`;
          const payload = {
            notification: {
              title,
              body,
            },
          };
          // Envoie la notification
          await admin.messaging().sendToTopic(`add_${deal.creator}_${deal.name}_${likes}like`, payload);
          // Récupère l'utilisateur à partir de la collection "Creation"
          const creationSnap = await DB.collection(CREATION_COLLECTION)
              .where("dealRef", "==", dealRef)
              .limit(1)
              .get();
          let user;
          if (!creationSnap.empty) {
            user = creationSnap.docs[0].data().user;
          }
          // Crée une alerte dans la collection "Alerte"
          await DB.collection(ALERTE_COLLECTION).add({
            title,
            body,
            dealRef,
            user,
            timestamp: admin.firestore.FieldValue.serverTimestamp(),
          });
        }
        return null;
      } else {
        return null;
      }
    });

exports.updateLikes = functions.firestore
    .document("Opinion/{likeId}")
    .onWrite((change, context) => {
      const data = change.after.data();
      const dealRef = data.dealRef;

      // Get the number of likes for the deal
      return admin.firestore().collection(OPINION_COLLECTION)
          .where("dealRef", "==", dealRef)
          .where("isLike", "==", true)
          .get()
          .then((querySnapshot) => {
            const numLikes = querySnapshot.size;

            // Update the number of likes for the deal
            return dealRef.update({likes: numLikes});
          });
    });

exports.initializeLikes = functions.firestore
    .document("Deal/{dealId}")
    .onCreate((snap, context) => {
      return snap.ref.update({likes: 0});
    });


