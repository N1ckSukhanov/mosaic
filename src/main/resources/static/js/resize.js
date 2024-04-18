let imageInput = document.querySelector("#images")
let cropperWindow = document.querySelector("#cropper")
let croppedImage  = document.querySelector("#croppedImage")
let saveButton = document.querySelector("#cropped")
let imageName = document.querySelector("#name-image")
let cropper
let imageUrl

		let reader = new FileReader();
		reader.onload = e => {
			if (e.target.result) {
				let cropperWindowImg = document.createElement("img")
				cropperWindowImg.id = "cropper-window-img"
				cropperWindowImg.src = e.target.result
				cropperWindow.appendChild(cropperWindowImg)
				cropper = new Cropper(cropperWindowImg)
			}
		};

imageInput.addEventListener('change', e => {
	if (e.target.files.length) {
		reader.readAsDataURL(e.target.files[e.target.files.length-1]);
	}
});

saveButton.addEventListener('click', e => {
	e.preventDefault();

	let imgSrc = cropper.getCroppedCanvas({}).toDataURL();
	cropperWindow.removeChild(document.querySelector(".cropper-container"))
	croppedImage.classList.remove('hide');
	croppedImage.src = imgSrc;
	image_path = imgSrc

	fetch(croppedImage.src)
	.then(res => res.blob())
	.then(blob => {
		const file = new File([blob], imageInput.files[0].name, blob)
		let dt = new DataTransfer();
		dt.items.add(file);
		imageInput.files = dt.files
		imageName.textContent = imageInput.files[0].name
	})
});

