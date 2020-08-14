/**
 * This file is specific to create-account/applicant/index.html.
 * It renders the fields on the page dynamically.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../../../strings.en.js';
import {Auth} from '../../firebase-auth.js';
import {USER_TYPE_COOKIE_PARAM, USER_TYPE_APPLICANT} from '../../../common-functions.js';

const COMMONG_STRINGS = AppStrings['create-account'];
const STRINGS = AppStrings['create-applicant-account'];
const LOGIN_HOMEPAGE_PATH = '../index.html';
const HOMEPAGE_PATH = '../../../index.html';

window.onload = () => {
  Auth.subscribeToUserAuthenticationChanges(onLogIn, onLogOut, onDefault);
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

function onLogOut() {
  
}

function onDefault() {
  
}

/** Adds all the text to the fields on this page. */
function renderPageElements() {
  Auth.addPhoneSignInAndSignUpUI('phone-auth', HOMEPAGE_PATH, STRINGS['new-user-info']);
  // Auth.subscribeToUserAuthenticationChanges();

  const backButton = document.getElementById('back');
  backButton.innerText = COMMONG_STRINGS['back'];
}

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = LOGIN_HOMEPAGE_PATH;
});
