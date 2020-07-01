
/** Dynamically add the limits for choosing the new job post expiry. */
function addJobExpiryLimits() {
  const date = new Date();
  const year = date.getFullYear();
  const month= date.getMonth();
  const day = date.getDate();
  const min = year.concat('-', month, '-', day);
  const max = (year + 1).concat('-', month, '-', day);


  console.log('in here');
  const datePicker = document.getElementById('new-post-expiry');
  console.log('datepicker', datePicker);
  datePicker.setAttribute('min', min);
  datePicker.setAttribute('max', max);
}

/** Adds a new job listing by making a POST request to the /jobs servlet */
// function addJob() {
//   fetch('/jobs', {
//     method: 'POST',
//     headers: {'Content-Type': 'application/json'},
//     body: JSON.stringify(jobDetails),
//   }).then((response) => response.text()).then((data) => {

//   });
// }
