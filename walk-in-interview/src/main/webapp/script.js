
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

/** Dynamically add the limits for choosing the new job post expiry. */
function addJobExpiryLimits() {
  const date = new Date();
  const min = date.toISOString().substr(0, 10);
  date.setFullYear(date.getFullYear() + 1);
  const max = date.toISOString().substr(0, 10);

  const datePicker = document.getElementById('new-post-expiry');
  datePicker.setAttribute('min', min);
  datePicker.setAttribute('max', max);
}

/** Dynamically add the options for job duration. */
function addJobDurationOptions() {
  const jobDurationSelect = document.getElementById('new-job-duration');
  jobDurationSelect.options.length = 0;

  jobDurationSelect.options[0] = new Option('Other', '');
  for (key in JOB_DURATION) {
    if ({}.hasOwnProperty.call(JOB_DURATION, key)) {
      jobDurationSelect.options[jobDurationSelect.options.length] =
      new Option(JOB_DURATION[key], key);
    }
  }
}
