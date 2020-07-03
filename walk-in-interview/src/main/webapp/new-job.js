/**
 * This file is specific to new-job.html. It renders the fields on the
 * page dynamically, and it also makes the POST request when the form
 * is submitted.
 */

const NEW_JOB_TITLE_ID = 'new-job-title';
const NEW_JOB_ADDRESS_ID = 'new-job-address';
const NEW_JOB_DESCRIPTION_ID = 'new-job-description';
const NEW_JOB_PAY = {
  FREQUENCY_ID: 'new-job-pay-frequency',
  MIN_ID: 'new-job-pay-min',
  MAX_ID: 'new-job-pay-max',
};
const REQUIREMENTS_LIST_NAME = 'requirements-list';
const NEW_JOB_EXPIRY_ID = 'new-job-expiry';
const NEW_JOB_DURATION_ID = 'new-job-duration';

const HOMEPAGE_PATH = '/walk-in-interview/src/main/webapp/index.html';

/**
 * Adds a new job listing by making a POST request to the /jobs servlet.
 * @return {Promise} Fetch function or promise with reject if invalid params.
 */
function addJob() {
  const name = document.getElementById(NEW_JOB_TITLE_ID).value;
  const address = document.getElementById(NEW_JOB_ADDRESS_ID).value;
  const description = document.getElementById(NEW_JOB_DESCRIPTION_ID).value;
  const payFrequency = document.getElementById(NEW_JOB_PAY.FREQUENCY_ID).value;

  const payMin = document.getElementById(NEW_JOB_PAY.MIN_ID).valueAsNumber;
  const payMax = document.getElementById(NEW_JOB_PAY.MAX_ID).valueAsNumber;
  if (payMin > payMax) {
    return Promise.reject(new Error('min greater than max'));
  }

  const requirementsCheckboxes =
    document.getElementsByName(REQUIREMENTS_LIST_NAME);
  const requirementsList = [];
  requirementsCheckboxes.forEach(({checked, id}) => {
    if (checked) {
      requirementsList.push(id);
    }
  });

  const expiry = document.getElementById(NEW_JOB_EXPIRY_ID).value;
  const duration = document.getElementById(NEW_JOB_DURATION_ID).value;

  if (name === '' || address === '' || description === '' ||
    payFrequency === '' || payMin === '' || payMax === '' || expiry === '') {
    // TODO(issue/19): add more validation checks
    return Promise.reject(new Error('required field left blank'));
  }

  const jobDetails = {
    jobName: name,
    jobLocation: {
      address: address,
      lat: 1.3039, // TODO(issue/13): get these from places api
      lon: 103.8358,
    },
    jobDescription: description,
    jobPay: {
      frequency: payFrequency,
      min: payMin,
      max: payMax,
    },
    requirements: requirementsList,
    postExpiry: expiry,
  };

  if (duration !== '') {
    jobDetails.jobDuration = duration;
  }

  return fetch('/jobs', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(jobDetails),
  });
}

const submitButton = document.getElementById('new-job-submit');
submitButton. addEventListener('click', (_) => {
  addJob()
      .then((repsonse) => repsonse.text())
      .then((data) => {
        console.log('data', data);
        window.location.href= HOMEPAGE_PATH;
      })
      .catch((error) => {
        console.log('error', error);
      });
});
