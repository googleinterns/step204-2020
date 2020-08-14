/**
 * This file is specific to create-account/business/index.html.
 * It renders the fields on the page dynamically
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../../../strings.en.js';
import {Auth} from '../../firebase-auth.js';
import {USER_TYPE_COOKIE_PARAM, USER_TYPE_BUSINESS,
  setCookie, setErrorMessage} from '../../../common-functions.js';

const COMMONG_STRINGS = AppStrings['create-account'];
const STRINGS = AppStrings['create-business-account'];
const CREATE_ACCOUNT_INFO_PAGE_PATH = '/account-info/index.html';
const CREATE_ACCOUNT_HOMEPAGE_PATH = '../index.html';

const EMAIL_IN_USE_ERROR_CODE = 'auth/email-already-in-use';
const INVALID_EMAIL_ERROR_CODE = 'auth/invalid-email';
const OPERATION_NOT_ALLOWED_ERROR_CODE = 'auth/operation-not-allowed';
const WEAK_PASSWORD_ERROR_CODE = 'auth/weak-password';

window.onload = () => {
  renderPageElements();
};

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
  window.location.href = CREATE_ACCOUNT_HOMEPAGE_PATH;
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

  await Auth.createBusinessAccount(account, password)
      .catch((error) => {
        showErrorMessageFromError(error);
      });

  // Enables the button regardless of success or failure
  document.getElementById('submit').disabled = false;

  let response = await Auth.subscribeToUserAuthenticationChanges()
      .catch((error) => {
        console.log(error);
        setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ COMMONG_STRINGS['error-message'],
          /* includesDefault= */false);
      });

  console.log(response);

  if (response === "Successfully creates the session cookie") {
    // TODO(issue/100): set the cookie at the server side instead
    setCookie(USER_TYPE_COOKIE_PARAM, USER_TYPE_BUSINESS);
    window.location.href = CREATE_ACCOUNT_INFO_PAGE_PATH;
  }
});

/**
 * Displays the error messages according to different situations.
 *
 * @param {Error} error Error from sign in.
 */
function showErrorMessageFromError(error) {
  switch (error.code) {
    case EMAIL_IN_USE_ERROR_CODE:
      setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ STRINGS['user-in-use-error'],
          /* includesDefault= */false);
      break;
    case INVALID_EMAIL_ERROR_CODE:
      setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ STRINGS['invalid-email-error'],
          /* includesDefault= */false);
      break;
    case OPERATION_NOT_ALLOWED_ERROR_CODE:
      setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ STRINGS['operation-not-allowed-error'],
          /* includesDefault= */false);
      break;
    case WEAK_PASSWORD_ERROR_CODE:
      setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ STRINGS['weak-password-error'],
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
