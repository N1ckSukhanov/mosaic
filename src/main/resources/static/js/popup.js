// Get the modal
let modal = document.getElementsByClassName("popup-hidder")[0];
let input = document.getElementById("images");
let close = document.getElementById("cropped");


input.addEventListener("change", () => {
	modal.style.display = "block";
})

// When the user clicks on <span> (x), close the modal
close.onclick = function() {
  modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
} 
