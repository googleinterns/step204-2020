/**
 * This file is specific to rendering and add markers to a specific map.
 * It contains the functions necessary to the google maps api.
 */

/**
 * Creates a map in the provided html div.
 * @param {String} mapElementId The map element id.
 * @param {float} latitude The center latitude.
 * @param {float} longitude The center longitude.
 * @param {int} zoom The amount of zoom for the map.
 * @return {google.maps.Map} The map object created.
 */
function createMap(mapElementId, latitude, longitude, zoom) {
  if (mapElementId === '') {
    throw new Error('map element id should not be empty');
  }

  return new google.maps.Map(document.getElementById(mapElementId), {
    center: {lat: latitude, lng: longitude},
    zoom: zoom,
  });
}

/**
 * Drops the markers so that the user can see them falling.
 * The pins indicate the location of the job. You can also
 * click on them to get some information about them.
 * @param {google.maps.Map} map The map to add the marker to.
 * @param {Object} job The job to be added to the marker.
 * @return {google.maps.Marker} The marker created.
 */
function addMarker(map, job) {
  if (map === null) {
    throw new Error('map should not be null');
  }

  const jobLocation = job['jobLocation'];
  return new google.maps.Marker({
    position: {lat: jobLocation['latitude'], lng: jobLocation['longitude']},
    map: map,
    animation: google.maps.Animation.DROP,
    title: job['jobTitle'],
  });
}

export {createMap, addMarker};
