/**
 * This file is specific to log-in/applicant/index.html.
 * It renders the fields on the page dynamically.
 */

// TODO(issue/21): get the language from the browser
const CURRENT_LOCALE = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../../../strings.en.js';
import {Auth} from '../../firebase-auth.js';
import {USER_TYPE_COOKIE_PARAM, USER_TYPE_APPLICANT, 
  setCookie} from '../../../common-functions.js';

const AUTH_STRINGS = AppStrings['auth'];
const COMMONG_STRINGS = AppStrings['log-in'];
const STRINGS = AppStrings['applicant-log-in'];
const LOGIN_HOMEPAGE_PATH = '../index.html';
const HOMEPAGE_PATH = '../../../index.html';

window.onload = () => {
  Auth.subscribeToUserAuthenticationChanges(
    onLogIn, onLogOut, onLogInFailure, onLogOutFailure);
  renderPageElements();
};

/**
 * What to do after the user signed in and the session cookie is created.
 */
function onLogIn() {
  // TODO(issue/100): set the cookie at the server side instead
  setCookie(USER_TYPE_COOKIE_PARAM, USER_TYPE_APPLICANT);
  
  window.location.href = HOMEPAGE_PATH;
}

/**
 * UI related function to be executed after successfully signed out.
 */
function onLogOut() {
  // No UI change
}

/**
 * UI related function to be executed for user does not sign in successfully.
 */
function onLogInFailure() {
  // TODO(issue/102): replace with proper notification
  alert(AUTH_STRINGS['sign-in-failure']);
}

/**
 * UI related function to be executed for user does not sign out successfully.
 */
function onLogOutFailure() {
  console.log(AUTH_STRINGS['sign-out-failure'] + '\n Forced user to log out');
}

/** Adds all the text to the fields on this page. */
function renderPageElements() {
  Auth.addPhoneSignInAndSignUpUI(
    'phone-auth', HOMEPAGE_PATH,
    onNewUser, onExistingUser);

  const backButton = document.getElementById('back');
  backButton.innerText = COMMONG_STRINGS['back'];
}

/**
 * The function to be executed for new user log in.
 */
function onNewUser() {
  console.log('This is a new user');
  // Informs user
  // TODO(issue/102): replace with proper notification
  alert(STRINGS['new-user-info']);

  window.location.href = HOMEPAGE_PATH;
}

/**
 * The function to be executed for existing user log in.
 */
function onExistingUser() {
  console.log('This is not a new user');

  // Informs user
  // TODO(issue/102): replace with proper notification
  alert(AUTH_STRINGS['sign-in-success']);

  window.location.href = HOMEPAGE_PATH;
}

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = LOGIN_HOMEPAGE_PATH;
});
