
/**
 * This file is specific to account/edit-applicant-account.html. 
 * It renders the fields on the page dynamically.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../strings.en.js';
import {APPLICANT_ID_PARAM, APPLICANT_DETAILS_PARAM} from '../common-functions.js';

const HOMEPAGE_PATH = '../index.html';
const STRINGS = AppStrings['applicant'];

window.onload = () => {
  renderPageElements();
};

function renderPageElements() {
  const applicantId = getApplicantId();
  const applicantDetails = getApplicantDetails();
  console.log(applicantDetails);

  const backButton = document.getElementById('back');
  backButton.innerText = STRINGS['back'];
}

/**
 * Gets the applicantId from the url.
 * @return {String} The applicantId.
 */
function getApplicantId() {
  const queryString = window.location.search;

  const urlParams = new URLSearchParams(queryString);
  if (urlParams === '') {
    throw new Error('url params should not be empty');
  }

  return urlParams.get(APPLICANT_ID_PARAM);
}

/**
 * Gets the applicant details from the url.
 * @return {JSON} The applicant detail json.
 */
function getApplicantDetails() {
  const queryString = window.location.search;

  const urlParams = new URLSearchParams(queryString);
  if (urlParams === '') {
    throw new Error('url params should not be empty');
  }

  return JSON.parse(urlParams.get(APPLICANT_DETAILS_PARAM));
}

const backButton = document.getElementById('back');
backButton.addEventListener('click', (_) => {
  window.location.href = HOMEPAGE_PATH;
});
