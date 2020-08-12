/**
 * This file will handle all the functions related to firebase auth.
 *
 * Note that the following lines would need to be added to any html
 * file that wishes to use firebase auth:
 * <script src="https://www.gstatic.com/firebasejs/7.17.1/firebase-app.js"></script>
 * <script src="https://www.gstatic.com/firebasejs/7.17.1/firebase-auth.js"></script>
 */

import {AppStrings} from '../strings.en.js';
import {setCookie, getCookie,
  USER_TYPE_COOKIE_PARAM, TYPE_NO_USER, TYPE_APPLICANT} from '../common-functions.js';
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
};

/**
 * This will sign into an existing business account.
 *
 * @param {String} email The email for the exisiting business account.
 * @param {String} password The password for the existing business account.
 */
Auth.signIntoBusinessAccount = (email, password) => {
  return firebase.auth().signInWithEmailAndPassword(email, password);
};

/**
 * This will add the firebase ui for phone authentication
 * to the provided element.
 *
 * @param {String} elementId The div element to add the UI.
 * @param {String} successPath The url for redirect on login success.
 * @param {String} newUserInfo The message to be displayed if the it is a new user.
 */
Auth.addPhoneSignInAndSignUpUI = (elementId, successPath, newUserInfo) => {
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

        // TODO(issue/100): set the cookie at the server side instead
        setCookie(USER_TYPE_COOKIE_PARAM, TYPE_APPLICANT);

        if (authResult.additionalUserInfo.isNewUser) {
          // Informs user
          alert(newUserInfo);
        }

        return true;
      },

      signInFailure: (error) => {
        console.error(error.message);

        alert(STRINGS['sign-in-failure']);
      }
    },
    signInSuccessUrl: successPath,
  });
};

/**
 * Signs out the current user.
 */
Auth.signOutCurrentUser = () => {
  firebase.auth().signOut().then(() => {
    Auth.subscribeToUserAuthenticationChanges();
    console.log('sign out successful');
    // TODO(issue/100): set the cookie at the server side instead
    setCookie(USER_TYPE_COOKIE_PARAM, TYPE_NO_USER);
    alert(STRINGS['sign-out-success']);
  }).catch((error) => {
    console.error(error);
  });
};

/**
 * Checks the user sign in status.
 * Makes a POST request to create a session cookie when the user signs in.
 * Makes a POST request to clear the session cookie when the user signs out.
 */
Auth.subscribeToUserAuthenticationChanges = () => {
  // To make sure it only triggered once when sign in and out
  var authFlag = true;

  firebase.auth().onAuthStateChanged((firebaseUser) => {
    if (authFlag) {
      authFlag = false;

      if (!firebaseUser) {
        // User not signed in.
        console.log('User Not Signed In');
        // Clears the session cookie
        Auth.postIdTokenToSessionLogout(API['log-out'])
            .catch((error) => {
              console.error(error);
            });
      }
        
      // User signed in.
      console.log('User Signed In');
      // Get the user's ID token as it is needed to exchange
      // for a session cookie.
      firebaseUser.getIdToken()
          .then((idToken) => {
            // Session login endpoint is queried and session cookie is set.
            // CSRF protection should be taken into account.
            const csrfToken = getCookie('csrfToken');

            if (idToken == localStorage.getItem('idToken') && csrfToken == localStorage.getItem('csrfToken')) {
              continue;
            }

            if (idToken != localStorage.getItem('idToken')) {
              localStorage.setItem('idToken', idToken);
            }

            if (csrfToken != localStorage.getItem('csrfToken')) {
              localStorage.setItem('csrfToken', csrfToken);
            }

            return Auth.postIdTokenToSessionLogin(API['log-in'],
                idToken, csrfToken);
          })
          .catch((error) => {
            console.error(error);
          });
    }
  });
};

/**
 * Makes a POST request to session log in endpoint.
 *
 * @param {String} url Login endpoint.
 * @param {String} idToken Id token.
 * @param {String} csrfToken CSRF token.
 * @return {Promise} Makes POST request.
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

/**
 * Makes a POST request to session log out endpoint.
 *
 * @param {String} url Login endpoint.
 * @return {Promise} Makes POST request.
 */
Auth.postIdTokenToSessionLogout = (url) => {
  return fetch(url, {
    method: 'POST',
    credentials: 'include',
  });
};

export {Auth};
