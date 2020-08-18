/**
 * This file is specific to index.html for job-details. It renders the fields
 * on the page dynamically, and it also makes the GET request to get the job
 * details.
 */

// TODO(issue/21): get the language from the browser
const CURRENT_LOCALE = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../strings.en.js';

import {JOB_ID_PARAM, USER_TYPE_COOKIE_PARAM, 
  USER_TYPE_APPLICANT, USER_TYPE_BUSINESS,
  setErrorMessage, getRequirementsList, 
  getCookie, deleteCookies} from '../common-functions.js';
import {createMap, addMarker, JOB_MAP_ZOOM} from '../maps.js';

import {API} from '../apis.js';
import {Auth} from '../account/firebase-auth.js';

const UPDATE_JOB_PATH = '../update-job/index.html';
const HOMEPAGE_PATH = '../index.html';

const AUTH_STRINGS = AppStrings['auth'];
const COMMON_STRINGS = AppStrings['common'];
const STRINGS = AppStrings['job-details'];
const JOB_STRINGS = AppStrings['job'];

let map;

window.onload = () => {
  Auth.subscribeToUserAuthenticationChanges(
    onLogIn, onLogOut, onLogInFailure, onLogOutFailure);
  renderJobDetailsPageElements();
};

/**
 * UI related function to be executed after successfully signed in.
 */
function onLogIn() {
  clearHeaderUI();

  const userType = getCookie(USER_TYPE_COOKIE_PARAM);
  if (userType === USER_TYPE_APPLICANT) {
    renderApplicantUI();
  } else if (userType === USER_TYPE_BUSINESS) {
    renderBusinessUI();
  } else {
    renderDefaultUI();
  }
}

/**
 * UI related function to be executed after successfully signed out.
 */
function onLogOut() {
  clearHeaderUI();

  renderLogOutUI();
}

/**
 * UI related function to be executed for user does not sign in successfully.
 */
function onLogInFailure() {
  clearHeaderUI();

  renderLogOutUI();

  alert(AUTH_STRINGS['sign-in-failure']);
}

/**
 * UI related function to be executed for user does not sign out successfully.
 */
function onLogOutFailure() {
  clearHeaderUI();

  // Clears the cookie, which also forces the user to log out
  deleteCookies();

  renderLogOutUI();

  console.log(AUTH_STRINGS['sign-out-failure'] + '\n Forced user to log out');
}

/**
 * Removes all html elements in header container
 */
function clearHeaderUI() {
  const headerContainer = document.getElementById('header-container');

  while(headerContainer.firstChild){
    headerContainer.removeChild(headerContainer.firstChild);
  }
}

/**
 * Renders the header UI for applicant user
 */
function renderApplicantUI() {
  renderBackButton();
}

/**
 * Renders the header UI for business user
 */
function renderBusinessUI() {
  renderBackButton();
  renderUpdateButton();
  renderDeleteButton();
}

/**
 * Renders the header UI for log out status
 */
function renderLogOutUI() {
  renderBackButton();
}

/**
 * Renders the back button
 */
function renderBackButton() {
  const headerContainer = document.getElementById('header-container');

  const homepageButton = document.createElement('button');
  homepageButton.setAttribute('id', 'back-to-homepage');
  homepageButton.innerText = STRINGS['back-to-homepage'];
  homepageButton.addEventListener('click', (_) => {
    window.location.href= HOMEPAGE_PATH;
  });

  headerContainer.appendChild(homepageButton);
}

/**
 * Renders the update button
 */
function renderUpdateButton() {
  const headerContainer = document.getElementById('header-container');

  const jobId = getJobId();
  if (jobId === '') {
    setErrorMessage('error-message', /* msg= */ STRINGS['error-message'],
        /* includesDefaultMsg= */ false);
    throw new Error('jobId should not be empty');
  }

  const updateForm = document.createElement('form');
  updateForm.setAttribute('id', 'update-form');
  updateForm.method = 'GET';
  updateForm.action = UPDATE_JOB_PATH;

  const jobIdElement = document.createElement('input');
  jobIdElement.setAttribute('id', 'job-id');
  jobIdElement.setAttribute('type', 'hidden');
  jobIdElement.setAttribute('name', JOB_ID_PARAM);
  jobIdElement.setAttribute('value', jobId);
  updateForm.appendChild(jobIdElement);

  const updateButton = document.createElement('input');
  updateButton.setAttribute('id', 'update');
  updateButton.setAttribute('class', 'button');
  updateButton.setAttribute('value', STRINGS['update']);
  updateButton.setAttribute('type', 'submit');
  // in case it was disabled earlier
  updateButton.disabled = false;
  updateForm.appendChild(updateButton);

  headerContainer.appendChild(updateForm);
}

/**
 * Renders the delete button
 */
function renderDeleteButton() {
  const headerContainer = document.getElementById('header-container');

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.setAttribute('id', 'delete');
  deleteButtonElement.innerText = STRINGS['delete'];
  // in case it was disabled earlier
  deleteButtonElement.disabled = false;

  deleteButtonElement.addEventListener('click', () => {
    const jobId = getJobId();
    if (jobId === '') {
      setErrorMessage(/* errorMessageElementId= */'error-message',
          /* msg= */ COMMON_STRINGS['empty-job-id-error-message'],
          /* includesDefault= */false);
      return;
    }
  
    deleteJobPost(jobId);
  });

  headerContainer.appendChild(deleteButtonElement);
}

/**
 * Add the current job (given the jobId) details to the page,
 * as well as the other elements on the page.
 */
async function renderJobDetailsPageElements() {
  const jobId = getJobId();
  if (jobId === '') {
    setErrorMessage('error-message', /* msg= */ STRINGS['error-message'],
        /* includesDefaultMsg= */ false);
    throw new Error('jobId should not be empty');
  }

  const job = await getJobDetails(jobId)
      .catch((error) => {
        console.error('error fetching job post', error);
        setErrorMessage('error-message', /* msg= */ STRINGS['error-message'],
            /* include default msg= */ false);
        // disable the buttons if theres an error
        updateButton.disabled = true;
        deleteButtonElement.disabled = true;
      });

  const location = job['jobLocation'];
  map = createMap('jobpage-map', location['latitude'], location['longitude'],
      JOB_MAP_ZOOM);

  displayJobDetails(job);
}

/**
 * This will add the details of the job post onto the page.
 * @param {Object} job The job details.
 */
function displayJobDetails(job) {
  if (job === undefined || job.length === 0) {
    setErrorMessage('error-message', /* msg= */ STRINGS['error-message'],
        /* includesDefaultMsg= */ false);

    // disable the buttons if theres an error
    if (document.body.contains(document.getElementById('update'))) {
      document.getElementById('update').disabled = true;
    }

    if (document.body.contains(document.getElementById('delete'))) {
      document.getElementById('delete').disabled = true;
    }

    return;
  }

  // in case they were disabled earlier
  if (document.body.contains(document.getElementById('update'))) {
    document.getElementById('update').disabled = false;
  }

  if (document.body.contains(document.getElementById('delete'))) {
    document.getElementById('delete').disabled = false;
  }

  // does not require info window
  addMarker(map, job);

  const jobTitle = document.getElementById('job-title');
  jobTitle.innerText = job['jobTitle'];

  const jobAddress = document.getElementById('address');
  const location = job['jobLocation'];
  jobAddress.innerText = `${location['address']}, ${location['postalCode']}`;

  const jobDescription = document.getElementById('description');
  jobDescription.innerText = job['jobDescription'];

  const jobPayTitle = document.getElementById('pay-title');
  jobPayTitle.innerText = `${JOB_STRINGS['pay-title']}:`;

  const pay = job['jobPay'];
  const jobPayMin = document.getElementById('pay-min');
  jobPayMin.innerText = pay['min'];

  const jobPayMax = document.getElementById('pay-max');
  jobPayMax.innerText = pay['max'];

  const payFrequencyOptions = JOB_STRINGS['pay-frequency'];
  const jobFrequency = document.getElementById('pay-frequency');
  jobFrequency.innerText = `(${payFrequencyOptions[pay['paymentFrequency']]})`;

  const jobDurationTitle = document.getElementById('duration-title');
  jobDurationTitle.innerText = `${JOB_STRINGS['duration-title']}:`;

  const durationOptions = JOB_STRINGS['duration'];
  const jobDuration = document.getElementById('duration');
  jobDuration.innerText = durationOptions[job['jobDuration']];

  const requirementsTitle = document.getElementById('requirements-title');
  requirementsTitle.innerText = `${JOB_STRINGS['requirements-title']}:`;

  const requirementsListElement = document.getElementById('requirements-list');
  // incase there is already something there
  requirementsListElement.innerHTML = '';

  const requirementsList = getRequirementsList();
  const requirementTemplate =
    document.getElementById('requirement-element-template');

  const jobRequirements = job['requirements'];
  for (const key in jobRequirements) {
    if (jobRequirements.hasOwnProperty(key)) {
      if (jobRequirements[key] /* is true */) {
        const requirementElement = requirementTemplate
            .cloneNode(/* and child elements */ true);
        requirementElement.setAttribute('id', key);
        requirementElement.innerText = requirementsList[key];

        requirementsListElement.appendChild(requirementElement);
      }
    }
  }
}

/**
 * Makes GET request to retrieve the individual job post from the database
 * given the jobId.
 * @param {String} jobId The jobId of the Job we want to get.
 * @return {Object} The Job object returned from the servlet.
 */
function getJobDetails(jobId) {
  return fetch(`${API['get-individual-job']}?jobId=${jobId}`)
      .then((response) => response.json())
      .then((data) => {
        console.log('data', data);
        /* reset the error (there might have been an error msg from earlier) */
        setErrorMessage('error-message', /* msg= */ '',
            /* includesDefaultMsg= */ false);
        return data;
      });
}

/**
 * Gets job Id of the current job post.
 * @return {String} The jobId.
 */
function getJobId() {
  const queryString = window.location.search;

  const urlParams = new URLSearchParams(queryString);
  if (urlParams === '') {
    throw new Error('url params should not be empty');
  }

  return urlParams.get(JOB_ID_PARAM);
}

/**
 * Tells the server to delete the this job post.
 * @param {String} jobId Job id for this job post.
 */
function deleteJobPost(jobId) {
  fetch(API['delete-job'], {method: 'PATCH', body: jobId})
      .then((response) => response.text())
      .then((data) => {
        console.log('data', data);
        /* reset the error (there might have been an error msg from earlier) */
        setErrorMessage(/* errorMessageElementId= */'error-message',
            /* msg= */ '', /* includesDefault= */false);
        window.location.href= HOMEPAGE_PATH;
      })
      .catch((error) => {
        setErrorMessage(/* errorMessageElementId= */'error-message',
            /* msg= */ STRINGS['delete-error-message'],
            /* includesDefault= */false);
        console.error(error);
      });
}
