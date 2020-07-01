
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


/** Adds a new job listing by making a POST request to the /jobs servlet */
function addJob() {
  fetch('/jobs', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(jobDetails),
  }).then((response) => response.text()).then((data) => {

  });
}
