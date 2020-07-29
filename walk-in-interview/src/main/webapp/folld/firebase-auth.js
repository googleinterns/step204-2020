// h
// document.addEventListener('DOMContentLoaded', function() {
    console.log('ni doc")');
window.onload = () => {
    console.log('in heree');
  // // The Firebase SDK is initialized and available here!
  //
  // firebase.auth().onAuthStateChanged(user => { });
  // firebase.database().ref('/path/to/ref').on('value', snapshot => { });
  // firebase.messaging().requestPermission().then(() => { });
  // firebase.storage().ref('/path/to/ref').getDownloadURL().then(() => { });

  firebase.auth().createUserWithEmailAndPassword('riyanar@gmail.com', 'pas234234s')
      .catch(function(error) {
        // Handle Errors here.
        console.log('errorr', error);
        // ...
      });

  //
  // // 🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥

  try {
    const app = firebase.app();
    const features = ['auth'].filter((feature) => typeof app[feature] === 'function');
    document.getElementById('load').innerHTML = `Firebase SDK loaded with ${features.join(', ')}`;
  } catch (e) {
    console.error(e);
    document.getElementById('load').innerHTML = 'Error loading the Firebase SDK, check the console.';
  }
};
