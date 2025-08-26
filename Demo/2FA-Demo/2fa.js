const express = require("express");
const speakeasy = require("speakeasy");
const qrcode = require("qrcode");
const app = express();

app.use(express.json());

let user = {};

app.get("/generate-secret", (req, res) => {
    const secret = speakeasy.generateSecret({ length: 20 });
    user.secret = secret.base32;
    qrcode.toDataURL(secret.otpauth_url, (err, imageUrl) => {
        user.qrCodeUrl = imageUrl;
        res.json({ secret: secret.base32, qrCodeUrl: imageUrl });
    });
});

app.get("/qrcode", (req, res) => {
  if (!user.qrCodeUrl) {
    return res.send("Még nincs QR kód generálva. Menj először a /generate-secret-re.");
  }

  // Itt HTML-ben ágyazzuk be az img tagek közé
  res.send(`
    <html>
      <body>
        <h1>2FA QR kód</h1>
        <img src="${user.qrCodeUrl}" />
      </body>
    </html>
  `);
});

app.post("/verify", (req, res) => {
    const { token } = req.body;
    const verified = speakeasy.totp.verify({
        secret: user.secret,
        encoding: "base32",
        token,
        window: 1,
    });
    res.json({ success: verified });
});

const PORT = 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));