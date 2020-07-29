
const firebase = require('firebase');
require('firebase/auth');

window.onload = () => {
  // renderHomepageElements();
  firebase.auth().createUserWithEmailAndPassword('riyan@gmail.com', 'pass')
      .catch(function(error) {
        // Handle Errors here.
        const errorCode = error.code;
        const errorMessage = error.message;
        // ...
      });
};
