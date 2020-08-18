/**
 * This file is specific to create-account/applicant/index.html.
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
import {API} from '../../../apis.js';
import {USER_TYPE_COOKIE_PARAM, USER_TYPE_APPLICANT, 
  setCookie, setErrorMessage} from '../../../common-functions.js';

const AUTH_STRINGS = AppStrings['auth'];
const ACCOUNT_STRINGS = AppStrings['create-account'];
const STRINGS = AppStrings['create-applicant-account'];
const CREATE_ACCOUNT_INFO_PAGE_PATH = './account-info/index.html';
const CREATE_ACCOUNT_HOMEPAGE_PATH = '../index.html';
const BAD_REQUEST_STATUS_CODE = 400;

window.onload = () => {
  Auth.subscribeToUserAuthenticationChanges(
    onLogIn, onLogOut, onLogInFailure, onLogOutFailure);
  renderPageElements();
};

/**
 * What to do after the user signed in and the session cookie is created.
 */
async function onLogIn() {
  // TODO(issue/101): Display button according to log in status;

  // TODO(issue/100): set the cookie at the server side instead
  setCookie(USER_TYPE_COOKIE_PARAM, USER_TYPE_APPLICANT);

  await createEmptyAccount();

  // TODO(issue/102): replace with proper notification
  alert(STRINGS['new-user-info']);
  
  // Directs to the page to fill in applicant account info.
  window.location.href = CREATE_ACCOUNT_INFO_PAGE_PATH;
}

function onLogOut() {
  // TODO(issue/101): Display button according to log in status;

  // TODO(issue/102): replace with proper notification
  alert(AUTH_STRINGS['sign-out-success']);
}

function onLogInFailure() {
  // TODO(issue/101): Display button according to log in status;
}

function onLogOutFailure() {
  // TODO(issue/101): Display button according to log in status;
}

/** Adds all the text to the fields on this page. */
function renderPageElements() {
  Auth.addPhoneSignInAndSignUpUI('phone-auth', CREATE_ACCOUNT_INFO_PAGE_PATH, STRINGS['new-user-info']);

  const backButton = document.getElementById('back');
  backButton.innerText = ACCOUNT_STRINGS['back'];
}

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = CREATE_ACCOUNT_HOMEPAGE_PATH;
});

/**
 * Creates an account with phoneNumber as name for the user
 */
async function createEmptyAccount() {
  var user = firebase.auth().currentUser;
  if (!user) {
    return new Promise.reject("Not signed in");
  }

  // Creates a totally empty account
  const accountDetails = {
    userType: USER_TYPE_APPLICANT,
    name: user.phoneNumber,
    // empty interest list
  };

  return fetch(API['create-applicant-account'], {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(accountDetails),
  })
      .then((response) => {
        if (response.status == BAD_REQUEST_STATUS_CODE) {
          setErrorMessage(/* errorMessageElementId= */'error-message',
              /* msg= */ ACCOUNT_STRINGS['create-account-error-message'],
              /* includesDefault= */false);
          throw new Error(ACCOUNT_STRINGS['create-account-error-message']);
        }

        /** reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* errorMessageElementId= */'error-message',
            /* msg= */ '', /* includesDefault= */false);

        // Enables the button regardless of success or failure
        document.getElementById('submit').disabled = false;

        // Directs to the page to fill in business account info.
        window.location.href = CREATE_ACCOUNT_INFO_PAGE_PATH;
      })
      .catch((error) => {
        // Not the server response error already caught and thrown
        if (error.message != ACCOUNT_STRINGS['create-account-error-message']) {
          console.log('error', error);

          setErrorMessage(/* errorMessageElementId= */'error-message',
              /* msg= */ ACCOUNT_STRINGS['error-message'],
              /* includesDefault= */false);
        }

        // Enables the button regardless of success or failure
        document.getElementById('submit').disabled = false;
      });
}
