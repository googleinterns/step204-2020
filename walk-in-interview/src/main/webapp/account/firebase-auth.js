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
  USER_TYPE_COOKIE_PARAM, USER_TYPE_NO_USER, USER_TYPE_APPLICANT} from '../common-functions.js';
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

// TODO(issue/21): get the language and country from the browser
const CURRENT_LOCALE = 'en';
const CURRENT_COUNTRY = 'SG';

firebase.auth().languageCode = CURRENT_LOCALE;

// Firebase UI
const ui = new firebaseui.auth.AuthUI(firebase.auth());

const STRINGS = AppStrings['auth'];

const Auth = {};

/**
 * This will create a new business account.
 *
 * @param {String} email The email for the new business account.
 * @param {String} password The password for the new business account.
 * Returns {Promise} A POST request to create an account.
 */
Auth.createBusinessAccount = (email, password) => {
  return firebase.auth().createUserWithEmailAndPassword(email, password);
};

/**
 * This will sign into an existing business account.
 *
 * @param {String} email The email for the exisiting business account.
 * @param {String} password The password for the existing business account.
 * Returns {Promise} A POST request to sign in an account.
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
 * @param {String} onNewUser The function to be executed for new user log in.
 * @param {String} onExistingUser The function to be executed for existing user log in.
 */
 Auth.addPhoneSignInAndSignUpUI = 
  (elementId, successPath, onNewUser, onExistingUser) => {
  ui.start(`#${elementId}`, {
    signInOptions: [
      {
        provider: firebase.auth.PhoneAuthProvider.PROVIDER_ID,
        defaultCountry: CURRENT_COUNTRY,
      },
    ],
    callbacks: {
      signInSuccessWithAuthResult: (authResult, redirectUrl) => {
        if (authResult.additionalUserInfo.isNewUser) {
          onNewUser();
        } else {
          onExistingUser();
        }

        return false;
      },

      signInFailure: (error) => {
        console.error(error.message);

        // TODO(issue/102): replace with proper notification
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
    console.log('sign out successful');
    // TODO(issue/100): set the cookie at the server side instead
    setCookie(USER_TYPE_COOKIE_PARAM, USER_TYPE_NO_USER);
  }).catch((error) => {
    console.error(error);

    // TODO(issue/102): replace with proper notification
    alert(STRINGS['sign-out-failure']);
  });
};

/**
 * Checks the user sign in status.
 * Makes a POST request to create a session cookie when the user signs in.
 * Makes a POST request to clear the session cookie when the user signs out.
 * 
 * @param {Function} onLogIn UI related function to be executed after successfully signed in.
 * @param {Function} onLogOut UI related function to be executed after successfully signed out.
 * @param {Function} onLogInFailure UI related function to be executed for user does not sign in successfully.
 * @param {Function} onLogOutFailure UI related function to be executed for user does not sign out successfully.
 */
Auth.subscribeToUserAuthenticationChanges = (onLogIn, onLogOut, onLogInFailure, onLogOutFailure) => {
  firebase.auth().onAuthStateChanged(async (firebaseUser) => {
    // User not signed in.
    if (!firebaseUser) {
      if (localStorage.getItem('sessionCookie') != 'true') {
        // Already signed out and cleared the session cookie, no need operation
        console.log('Already signed out');
        return;
      }
      
      // Clears the session cookie
      Auth.clearSessionCookie(onLogOut, onLogOutFailure);

      console.log('Successfully signed out');

      return;
    }

    // User signed in.

    if (localStorage.getItem('sessionCookie') == 'true') {
      // Already signed in and created session cookie, no need operation
      console.log('Already signed in');
      return;
    }
      
    // Get the user's ID token as it is needed to exchange
    // for a session cookie.
    await Auth.createSessionCookie(firebaseUser, onLogIn, onLogInFailure);

    console.log('Successfully signed in');
  });
};

/**
 * Clears the session cookie
 * 
 * @param {Function} onLogOut UI related function to be executed after successfully signed out.
 * @param {Function} onLogOutFailure UI related function to be executed for user does not sign out successfully.
 */
Auth.clearSessionCookie = async (onLogOut, onLogOutFailure) => {
  try {
    // Clears the session cookie
    let response = await Auth.postIdTokenToSessionLogout(API['log-out']);

    if (!response.ok) {
      throw new Error(`HTTP Error: ${response.statusCode}`);
    }

    localStorage.setItem('sessionCookie', 'false');
    console.log('Successfully clears the session cookie');

    // Changes the UI accordingly.
    onLogOut();
  } catch (error) {
    console.log(error);

    // Displays the default UI.
    onLogOutFailure();
  }
};

/**
 * Gets the user's ID token as it is needed to exchange for a session cookie.
 * 
 * @param {user} firebaseUser Current user.
 * @param {Function} onLogIn UI related function to be executed after successfully signed in.
 * @param {Function} onLogInFailure UI related function to be executed for user does not sign in successfully.
 */
Auth.createSessionCookie = (firebaseUser, onLogIn, onLogInFailure) => {
  return firebaseUser.getIdToken()
      .then(async (idToken) => {
        // Session login endpoint is queried and session cookie is set.
        // CSRF protection should be taken into account.
        const csrfToken = getCookie('csrfToken');

        try {
          let response = await Auth.postIdTokenToSessionLogin(API['log-in'], idToken, csrfToken);

          if (!response.ok) {
            throw new Error(`HTTP Error: ${response.statusCode}`);
          }

          localStorage.setItem('sessionCookie', 'true');
          console.log('Successfully creates the session cookie');

          // Changes the UI accordingly.
          onLogIn();
        } catch(error) {
          console.log(error);

          // Displays the UI accordingly.
          onLogInFailure();
        }
      })
      .catch((error) => {
        console.log(error);

        // Displays the UI accordingly.
        onLogInFailure();
      });
};

/**
 * Makes a POST request to session log in endpoint.
 *
 * @param {String} url Login endpoint.
 * @param {String} idToken Id token.
 * @param {String} csrfToken CSRF token.
 * Returns {Promise} Makes POST request.
 */
Auth.postIdTokenToSessionLogin = (url, idToken, csrfToken) => {
  const params = new URLSearchParams();
  params.append('idToken', idToken);
  params.append('csrfToken', csrfToken);

  return fetch(url, {
    method: 'POST',
    header: {'Set-Cookie': 'Secure;SameSite=None'},
    body: params,
    credentials: 'include',
  });
};

/**
 * Makes a POST request to session log out endpoint.
 *
 * @param {String} url Login endpoint.
 * Returns {Promise} Makes POST request.
 */
Auth.postIdTokenToSessionLogout = (url) => {
  return fetch(url, {
    method: 'POST',
    header: {"Set-Cookie": "Secure;SameSite=None"},
    credentials: 'include',
  });
};

export {Auth};
