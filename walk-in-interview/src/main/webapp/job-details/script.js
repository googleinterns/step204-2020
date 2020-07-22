/**
 * This file is specific to index.html for job-details. It renders the fields
 * on the page dynamically, and it also makes the GET request to get the job
 * details.
 */

// TODO(issue/21): get the language from the browser
const CurrentLocale = 'en';

/**
 * Import statements are static so its parameters cannot be dynamic.
 * TODO(issue/22): figure out how to use dynamic imports
 */
import {AppStrings} from '../strings.en.js';

import {setErrorMessage,
  getRequirementsList} from '../common-functions.js';

const UPDATE_JOB_PATH = '../update-job/index.html';
const HOMEPAGE_PATH = '../index.html';

const COMMON_STRINGS = AppStrings['common'];
const STRINGS = AppStrings['job-details'];
const NEW_JOB_STRINGS = AppStrings['new-job'];

window.onload = () => {
  renderJobDetailsPageElements();
};

/**
 * Add the current job (given the jobId) details to the page,
 * as well as the other elements on the page.
 */
async function renderJobDetailsPageElements() {
  const homepageButton = document.getElementById('back-to-homepage');
  homepageButton.innerText = STRINGS['back-to-homepage'];
  homepageButton.addEventListener('click', (_) => {
    window.location.href= HOMEPAGE_PATH;
  });

  const updateButton = document.getElementById('update');
  updateButton.setAttribute('value', STRINGS['update']);
  updateButton.setAttribute('type', 'submit');

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
      });

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
    return;
  }

  const jobTitle = document.getElementById('job-title');
  jobTitle.innerText = job['jobTitle'];

  const jobAddress = document.getElementById('address');
  const location = job['jobLocation'];
  jobAddress.innerText = `${location['address']}, ${location['postalCode']}`;

  const jobDescription = document.getElementById('description');
  jobDescription.innerText = job['jobDescription'];

  const jobPayTitle = document.getElementById('pay-title');
  jobPayTitle.innerText = `${NEW_JOB_STRINGS['pay-title']}:`;

  const pay = job['jobPay'];

  const jobPayMin = document.getElementById('pay-min');
  jobPayMin.innerText = pay['min'];

  const jobPayMax = document.getElementById('pay-max');
  jobPayMax.innerText = pay['max'];

  const payFrequencyOptions = NEW_JOB_STRINGS['pay-frequency'];
  const jobFrequency = document.getElementById('pay-frequency');
  jobFrequency.innerText = `(${payFrequencyOptions[pay['paymentFrequency']]})`;

  const jobDurationTitle = document.getElementById('duration-title');
  jobDurationTitle.innerText = `${NEW_JOB_STRINGS['duration-title']}:`;

  const durationOptions = NEW_JOB_STRINGS['duration'];
  const jobDuration = document.getElementById('duration');
  jobDuration.innerText = durationOptions[job['jobDuration']];

  const requirementsTitle = document.getElementById('requirements-title');
  requirementsTitle.innerText = `${NEW_JOB_STRINGS['requirements-title']}:`;

  const requirementsListElement = document.getElementById('requirements-list');
  // incase there is already something there
  requirementsListElement.innerHTML = '';

  const requirementsList = getRequirementsList();
  const requirementTemplate =
    document.getElementById('requirement-element-template');
  job['requirements'].forEach((req) => {
    const requirementElement = requirementTemplate
        .cloneNode(/* and child elements */ true);
    requirementElement.setAttribute('id', req);
    requirementElement.innerText = requirementsList[req];

    requirementsListElement.appendChild(requirementElement);
  });
}

/**
   * Makes GET request to retrieve the individual job post from the database
   * given the jobId.
   * @param {String} jobId The jobId of the Job we want to get.
   * @return {Object} The Job object returned from the servlet.
   */
function getJobDetails(jobId) {
  return fetch(`/jobs?jobId=${jobId}`)
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
  // TODO(issue/53): implement this function
  return 'Not Implemented';
}

const updateButton = document.getElementById('update');
updateButton.addEventListener('click', (_) => {
  // TOOD(issue/xx): get once PR merged
});
