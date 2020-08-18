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
const HOMEPAGE_PATH = '../../../index.html';
const SUCCESS_STATUS_CODE = 200;

window.onload = () => {
  Auth.subscribeToUserAuthenticationChanges(
    onLogIn, onLogOut, onLogInFailure, onLogOutFailure);
  renderPageElements();
};

/**
 * What to do after the user signed in and the session cookie is created.
 */
function onLogIn() {
  // TODO(issue/101): Display button according to log in status;

  // TODO(issue/100): set the cookie at the server side instead
  setCookie(USER_TYPE_COOKIE_PARAM, USER_TYPE_APPLICANT);
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
  Auth.addPhoneSignInAndSignUpUI(
    'phone-auth', CREATE_ACCOUNT_INFO_PAGE_PATH,
    onNewUser, onExistingUser);

  const backButton = document.getElementById('back');
  backButton.innerText = ACCOUNT_STRINGS['back'];
}

/**
 * The function to be executed for new user log in.
 */
async function onNewUser() {
  console.log('This is a new user');

  await createPreliminaryApplicantAccount();

  // Informs user
  // TODO(issue/102): replace with proper notification
  alert(STRINGS['new-user-info']);

  // Directs to the page to fill in applicant account info
  window.location.href = CREATE_ACCOUNT_INFO_PAGE_PATH;
}

/**
 * The function to be executed for existing user log in.
 */
function onExistingUser() {
  console.log('This is not a new user');

  // Informs user
  // TODO(issue/102): replace with proper notification
  alert(STRINGS['non-new-user-info']);

  // Directs the user back to home page
  window.location.href = HOMEPAGE_PATH;
}

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = CREATE_ACCOUNT_HOMEPAGE_PATH;
});

/**
 * Creates a preliminary account object with phoneNumber as name for the user
 * Returns {Promise} of POST request
 */
async function createPreliminaryApplicantAccount() {
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
        if (response.status !== SUCCESS_STATUS_CODE) {
          setErrorMessage(/* errorMessageElementId= */'error-message',
              /* msg= */ ACCOUNT_STRINGS['create-account-error-message'],
              /* includesDefault= */false);
          throw new Error(ACCOUNT_STRINGS['create-account-error-message']);
        }

        /** reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* errorMessageElementId= */'error-message',
            /* msg= */ '', /* includesDefault= */false);

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
      });
}
