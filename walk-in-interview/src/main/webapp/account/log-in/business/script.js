/**
 * This file is specific to log-in/business/index.html.
 * It renders the fields on the page dynamically
 */

// TODO(issue/21): get the language from the browser
const CURRENT_LOCALE = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../../../strings.en.js';
import {Auth} from '../../firebase-auth.js';
import {USER_TYPE_COOKIE_PARAM, USER_TYPE_BUSINESS,
  setCookie, setErrorMessage} from '../../../common-functions.js';

const AUTH_STRINGS = AppStrings['auth'];
const COMMONG_STRINGS = AppStrings['log-in'];
const STRINGS = AppStrings['business-log-in'];
const LOGIN_HOMEPAGE_PATH = '../index.html';
const HOMEPAGE_PATH = '../../../index.html';

const INVALID_EMAIL_ERROR_CODE = 'auth/invalid-email';
const USER_DISABLED_ERROR_CODE = 'auth/user-disabled';
const USER_NOT_FOUND_ERROR_CODE = 'auth/user-not-found';
const WRONG_PASSWORD_ERROR_CODE = 'auth/wrong-password';

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
  setCookie(USER_TYPE_COOKIE_PARAM, USER_TYPE_BUSINESS);

  // TODO(issue/102): replace with proper notification
  alert(AUTH_STRINGS['sign-in-success']);

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
  const accountElement = document.getElementById('account');

  const accountLabelElement = accountElement.children[0];
  accountLabelElement.innerText = STRINGS['account'];

  const accountInputElement = accountElement.children[1];
  accountInputElement.setAttribute('name', 'account-input');
  accountInputElement.setAttribute('type', 'text');

  const passwordElement = document.getElementById('password');

  const passwordLabelElement = passwordElement.children[0];
  passwordLabelElement.innerText = STRINGS['password'];

  const passwordInputElement = passwordElement.children[1];
  passwordInputElement.setAttribute('name', 'password-input');
  passwordInputElement.setAttribute('type', 'password');

  const backButton = document.getElementById('back');
  backButton.innerText = COMMONG_STRINGS['back'];

  const submitButton = document.getElementById('submit');
  submitButton.innerText = COMMONG_STRINGS['submit'];
}

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = LOGIN_HOMEPAGE_PATH;
});

const submitButton = document.getElementById('submit');
submitButton.addEventListener('click', async (_) => {
  // Disables the button to avoid accidental double click
  document.getElementById('submit').disabled = true;

  const account = document.getElementById('account-input').value;

  if (account === '') {
    setErrorMessage(/* errorMessageElementId= */'error-message',
        /* msg= */ STRINGS['empty-email'],
        /* includesDefault= */false);
    return;
  }

  const password = document.getElementById('password-input').value;

  // Resets the error (there might have been an error msg from earlier)
  setErrorMessage(/* errorMessageElementId= */'error-message',
      /* msg= */ '',
      /* includesDefault= */false);

  await Auth.signIntoBusinessAccount(account, password)
      .catch((error) => {
        showErrorMessageFromError(error);
      });

  // Enables the button regardless of success or failure
  document.getElementById('submit').disabled = false;
});

/**
 * Displays the error messages according to different situations.
 *
 * @param {Error} error Error from sign in.
 */
function showErrorMessageFromError(error) {
  switch (error.code) {
    case INVALID_EMAIL_ERROR_CODE:
      setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ STRINGS['invalid-email-error'],
          /* includesDefault= */false);
      break;
    case USER_DISABLED_ERROR_CODE:
      setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ STRINGS['user-disabled'],
          /* includesDefault= */false);
      break;
    case USER_NOT_FOUND_ERROR_CODE:
      setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ STRINGS['user-not-found'],
          /* includesDefault= */false);
      break;
    case WRONG_PASSWORD_ERROR_CODE:
      setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ STRINGS['wrong-password'],
          /* includesDefault= */false);
      break;
    default:
      setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ COMMONG_STRINGS['error-message'],
          /* includesDefault= */false);
      console.error(error.message);
      break;
  }
}
