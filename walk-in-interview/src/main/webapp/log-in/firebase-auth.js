/**
 * This file will handle all the functions related to firebase auth.
 */

let app;
let features;

try {
  app = firebase.app();
  features = ['auth'].filter((feature) => typeof app[feature] === 'function');
  console.log(`Firebase SDK loaded with ${features.join(', ')}`);
} catch (e) {
  console.error(e);
}

window.onload = () => {
  createBusinessAccount('riyabusiness@gmail.com', 'business');
};

// window.onload = () => {
//   console.log('in onload');
//   // firebase.auth().onAuthStateChanged(user => { });
//   try {
//     app = firebase.app();
//     features = ['auth'].filter((feature) => typeof app[feature] === 'function');
//     console.log(`Firebase SDK loaded with ${features.join(', ')}`);
//   } catch (e) {
//     console.error(e);
//   }
// };

/**
 * This will create a new business account.
 * @param {String} email The email for the new business account.
 * @param {String} password The password for the new business account.
 */
function createBusinessAccount(email, password) {
  firebase.auth().createUserWithEmailAndPassword(email, password)
      .catch((error) => {
        console.error(error);
      });
}

/**
 * This will sign into an existing business account.
 * @param {String} email The email for the exisiting business account.
 * @param {String} password The password for the existing business account.
 */
function signIntoBusinessAccount(email, password) {
  firebase.auth().signInWithEmailAndPassword(email, password)
      .catch((error) => {
        console.error(error);
      });
}

export {createBusinessAccount, signIntoBusinessAccount};
