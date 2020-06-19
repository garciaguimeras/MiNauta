function() {
    img = document.getElementsByClassName('captcha')[0];
    canvas = document.createElement('canvas');
    canvas.width = img.width;
    canvas.height = img.height;
    ctx = canvas.getContext('2d');
    ctx.drawImage(img, 0, 0);
    bmp = ctx.getImageData(0, 0, canvas.width, canvas.height).data;

    obj = {
        bitmap: bmp,
        width: canvas.width,
        height: canvas.height
    };

    portal.captcha(JSON.stringify(obj));
}