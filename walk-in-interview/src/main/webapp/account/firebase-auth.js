/**
 * This file will handle all the functions related to firebase auth.
 *
 * Note that the following lines would need to be added to any html
 * file that wishes to use firebase auth:
 * <script src="https://www.gstatic.com/firebasejs/7.17.1/firebase-app.js"></script>
 * <script src="https://www.gstatic.com/firebasejs/7.17.1/firebase-auth.js"></script>
 */

import {AppStrings} from '../strings.en.js';
import {getCookie} from '../common-functions.js';
import {API} from '../apis.js';

const firebaseConfig = {
  apiKey: 'AIzaSyDhpzKNLAMNyEdw6ovQ5sPvnOhXDwhse-o',
  authDomain: 'com-walk-in-interview.firebaseapp.com',
  databaseURL: 'https://com-walk-in-interview.firebaseio.com',
  projectId: 'google.com:walk-in-interview',
  storageBucket: 'undefined',
  messagingSenderId: '610715446188',
  appId: '1:610715446188:web:8cec0c2798940bb9d32809',
  measurementId: 'G-RJ55G4ZXN8',
};

firebase.initializeApp(firebaseConfig);

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

firebase.auth().languageCode = CurrentLocale;

// Firebase UI
const ui = new firebaseui.auth.AuthUI(firebase.auth());

const STRINGS = AppStrings['auth'];

const Auth = {};

/**
 * This will create a new business account.
 *
 * @param {String} email The email for the new business account.
 * @param {String} password The password for the new business account.
 */
Auth.createBusinessAccount = (email, password) => {
  firebase.auth().createUserWithEmailAndPassword(email, password)
      .catch((error) => {
        console.error(error);
      });
  checkCurrentUser();
};

/**
 * This will sign into an existing business account.
 *
 * @param {String} email The email for the exisiting business account.
 * @param {String} password The password for the existing business account.
 * @return {*} Returns the function that makes the POST request.
 */
Auth.signIntoBusinessAccount = (email, password) => {
  return firebase.auth().signInWithEmailAndPassword(email, password)
      .then(({user}) => {
        // Get the user's ID token as it is needed to exchange
        // for a session cookie.
        return user.getIdToken()
            .then((idToken) => {
              // Session login endpoint is queried and session cookie is set.
              // CSRF protection should be taken into account.
              const csrfToken = getCookie('csrfToken');
              return Auth.postIdTokenToSessionLogin(API['log-in'],
                  idToken, csrfToken);
            });
      });
};

/**
 * This will create a new applicant account.
 *
 * @param {String} elementId The div element to add the UI.
 * @param {String} successPath The url for redirect on login success.
 * @param {String} newUserInfo The message to be displayed if the it is a new user.
 */
Auth.createApplicantAccount = (elementId, successPath, newUserInfo) => {
  Auth.addPhoneAuthUI(elementId, successPath, newUserInfo);

  var user = firebase.auth().currentUser;
  if (user) {
    // User is signed in.
    
    // Get the user's ID token as it is needed to exchange
    // for a session cookie.
    return user.getIdToken()
    .then((idToken) => {
      // Session login endpoint is queried and session cookie is set.
      // CSRF protection should be taken into account.
      const csrfToken = getCookie('csrfToken');
      return Auth.postIdTokenToSessionLogin(API['create-applicant-account'],
          idToken, csrfToken);
    });
  }
};

/**
 * This will sign into an existing applicant account.
 *
 * @param {String} elementId The div element to add the UI.
 * @param {String} successPath The url for redirect on login success.
 * @param {String} newUserInfo The message to be displayed if the it is a new user.
 */
Auth.signIntoApplicantAccount = (elementId, successPath, newUserInfo) => {
  Auth.addPhoneAuthUI(elementId, successPath, newUserInfo);

  var user = firebase.auth().currentUser;
  if (user) {
    // User is signed in.
    
    // Get the user's ID token as it is needed to exchange
    // for a session cookie.
    return user.getIdToken()
    .then((idToken) => {
      // Session login endpoint is queried and session cookie is set.
      // CSRF protection should be taken into account.
      const csrfToken = getCookie('csrfToken');
      return Auth.postIdTokenToSessionLogin(API['log-in'],
          idToken, csrfToken);
    });
  }
};

/**
 * This will add the firebase ui for phone authentication
 * to the provided element.
 *
 * @param {String} elementId The div element to add the UI.
 * @param {String} successPath The url for redirect on login success.
 * @param {String} newUserInfo The message to be displayed if the it is a new user.
 */
Auth.addPhoneAuthUI = (elementId, successPath, newUserInfo) => {
  ui.start(`#${elementId}`, {
    signInOptions: [
      {
        provider: firebase.auth.PhoneAuthProvider.PROVIDER_ID,
        defaultCountry: 'SG',
      },
    ],
    callbacks: {
      signInSuccessWithAuthResult: (authResult, redirectUrl) => {
        // TODO(issue/89): add new user pages for name/skills
        // for now, this will only redirect to homepage for exisiting users
        if (authResult.additionalUserInfo.isNewUser) {
          alert(newUserInfo);
        }

        return true;
      },
    },
    signInSuccessUrl: successPath,
  });
};

/**
 * Signs out the current user.
 *
 * @param {String} elementId The div in which to show the signed out status.
 */
Auth.signOutCurrentUser = (elementId) => {
  firebase.auth().signOut().then(() => {
    console.log('sign out successful');
    document.getElementById(elementId).innerText = STRINGS['sign-out-success'];
  }).catch((error) => {
    console.error(error);
  });
};

/**
 * Checks the user sign in status.
 */
Auth.checkCurrentUser = () => {
  firebase.auth().onAuthStateChanged((firebaseUser) => {
    if (firebaseUser) {
      console.log('signed in', firebaseUser);
    } else {
      console.log('not signed in');
    }
  });
};

/**
 * Makes a POST request to session log in endpoint.
 *
 * @param {String} url Login endpoint.
 * @param {String} idToken Id token.
 * @param {String} csrfToken CSRF token.
 * @return {*} Makes POST request.
 */
Auth.postIdTokenToSessionLogin = (url, idToken, csrfToken) => {
  const params = new URLSearchParams();
  params.append('idToken', idToken);
  params.append('csrfToken', csrfToken);

  return fetch(url, {
    method: 'POST',
    body: params,
    credentials: 'include',
  });
};

export {Auth};
