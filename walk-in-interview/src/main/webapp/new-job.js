
/**
 * Fields related to job pay.
 * @enum {stirng}
 */
const NEW_JOB_PAY = {
  FREQUENCY_ID: 'new-job-pay-frequency',
  MIN_ID: 'new-job-pay-min',
  MAX_ID: 'new-job-pay-max',
};

const NEW_JOB_EXPIRY_ID = 'new-job-expiry';
const NEW_JOB_DURATION_ID = 'new-job-duration';

/**
 * Options for job duration.
 * @enum {stirng}
 */
const JOB_DURATION = {
  ONE_WEEK: '1 Week',
  TWO_WEEKS: '2 Weeks',
  ONE_MONTH: '1 Month',
  SIX_MONTHS: '6 Months',
  ONE_YEAR: '1 Year',
};

/**
 * Options for frequency of job pay.
 * @enum {stirng}
 */
const JOB_PAY_FREQUENCY = {
  HOURLY: 'Hourly',
  WEEKLY: 'Weekly',
  MONTHLY: 'Monthly',
  YEARLY: 'Yearly',
};

/** Function to be called on loading of the "create new job" page. */
function addNewJobOptions() {
  addRequirementsList();
  addJobPayFrequencyOptions();
  addJobDurationOptions();
  addJobExpiryLimits();
}

/** Add the list of requirements that are stored in the database. */
function addRequirementsList() {
  // TODO(issue/17): get from the database and render dynamically
}

/** Dynamically add the options for job pay frequency. */
function addJobPayFrequencyOptions() {
  const jobPaySelect = document.getElementById(NEW_JOB_PAY.FREQUENCY_ID);
  jobPaySelect.options.length = 0;

  (jobPaySelect.options[0] = new Option('Select', ''))
      .setAttribute('disabled', true);
  addSelectOptions(jobPaySelect, JOB_PAY_FREQUENCY);
}

/** Dynamically add the options for job duration. */
function addJobDurationOptions() {
  const jobDurationSelect = document.getElementById(NEW_JOB_DURATION_ID);
  jobDurationSelect.options.length = 0;

  jobDurationSelect.options[0] = new Option('Other', '');
  addSelectOptions(jobDurationSelect, JOB_DURATION);
}

/**
 * Add the keys and values from the options map to the select element.
 * @param {Element} select The select element.
 * @param {Map} options The map of options to be added.
 */
function addSelectOptions(select, options) {
  for (key in options) {
    if ({}.hasOwnProperty.call(options, key)) {
      select.options[select.options.length] =
        new Option(options[key], key);
    }
  }
}

/** Dynamically add the limits for choosing the new job post expiry. */
function addJobExpiryLimits() {
  const date = new Date();
  const min = date.toISOString().substr(0, 10);
  date.setFullYear(date.getFullYear() + 1);
  const max = date.toISOString().substr(0, 10);

  const datePicker = document.getElementById(NEW_JOB_EXPIRY_ID);
  datePicker.setAttribute('min', min);
  datePicker.setAttribute('max', max);
}
