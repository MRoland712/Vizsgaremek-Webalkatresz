const jwt = require("jsonwebtoken");
const bcrypt = require("bcrypt");
const nodemailer = require("nodemailer");
const readline = require("readline");
const fs = require("fs");
const crypto = require('crypto');
const express = require('express')

//express szerver használatának engedélyezése
const app = express();
app.use(express.json());

// DEMO database (passw hashed)
const rawData = fs.readFileSync("Users.json", "utf-8");
const users = JSON.parse(rawData);

// HTML email sablon
const getEmailTemplate = (user, otp) => `
<!DOCTYPE html>
<html lang="hu">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 600px;
            margin: 20px auto;
            background-color: #ffffff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .header {
            text-align: center;
            padding-bottom: 20px;
        }
        .header img {
            max-width: 150px;
        }
        .content {
            font-size: 16px;
            line-height: 1.6;
            color: #333333;
        }
        .otp {
            font-size: 24px;
            font-weight: bold;
            color: #2c3e50;
            text-align: center;
            padding: 15px 0;
        }
        .warning {
            color: #e74c3c;
            font-weight: bold;
        }
        .footer {
            text-align: center;
            font-size: 14px;
            color: #777777;
            padding-top: 20px;
        }
        .button {
            display: inline-block;
            padding: 10px 20px;
            margin: 10px 0;
            background-color: #3498db;
            color: #ffffff;
            text-decoration: none;
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <img src="https://via.placeholder.com/150x50?text=CarComps" alt="CarComps Logo">
        </div>
        <div class="content">
            <p>Tisztelt ${user.full_name || user.username}!</p>
            <p>Az Ön egyszer használatos belépési kódja:</p>
            <div class="otp">${otp}</div>
            <p>Ez a kód <strong>1 óráig érvényes</strong>. Kérjük, kizárólag a hivatalos <a href="https://carcomps.hu">CarComps.hu</a> felületen használja.</p>
            <p class="warning">Kérjük, ne ossza meg senkivel ezt a kódot!</p>
            <p>Ha nem Ön kérte ezt a kódot, kérjük, haladéktalanul jelezze ügyfélszolgálatunknak az alábbi elérhetőségeken:</p>
            <p>
                <strong>Email:</strong> <a href="mailto:ugyfelszolgalat@carcomps.hu">ugyfelszolgalat@carcomps.hu</a><br>
                <strong>Weboldal:</strong> <a href="https://carcomps.hu/ugyfelszolgalat" class="button">Ügyfélszolgálat</a>
            </p>
        </div>
        <div class="footer">
            <p>Tisztelettel:<br>CarComps Ügyfélszolgálat</p>
        </div>
    </div>
</body>
</html>
`;

// OTP store
const otpStore = {};

//read AuthPass.env and deobfuscate
const text = fs.readFileSync("AuthPass.env", "utf-8").split('\n');
const push = Number(text[2]);
let text_user = text[0]
      .split('')
      .map(char => String.fromCharCode(char.charCodeAt(0) - push))
      .join("")
      .slice(0,-1);
let text_pass = text[1]      
      .split('')
      .map(char => String.fromCharCode(char.charCodeAt(0) - push))
      .join("")
      .slice(0,-1);
//console.log(`${push};${text_user}; ${text_pass}`)

// SMTP beállítások (példa Gmail-lel)
// Gmail-nél kell: https://myaccount.google.com/apppasswords
const transporter = nodemailer.createTransport({
  host: "smtp.gmail.com",
  port: 587,
  secure: false, // STARTTLS
  auth: {
    user: text_user,
    pass: text_pass
  }
});

// 1. Belépési kérelem (OTP generálás és küldés)
function requestLogin(username, password) {
  const user = users.find((u) => u.username === username);
  if (!user) return { error: "Hibás felhasználónév vagy jelszó!" };

  if (!bcrypt.compareSync(password, user.passwordHash)) {
    return { error: "Hibás felhasználónév vagy jelszó!" };
  }

  const otp = Math.floor(100000 + Math.random() * 900000).toString();
  otpStore[username] = otp;

transporter.sendMail(
    {
        from: '"Biztonsági rendszer" <noreply@carcomps.hu>',
        to: user.email,
        subject: "CarComps - Belépési kód",
        html: getEmailTemplate(user, otp)
    },
    (err, info) => {
        if (err) {
            console.error("Email küldési hiba:", err);
        } else {
            //console.log("OTP elküldve:", info.response);
        }
    }
);

  return { message: "OTP elküldve!" };
}

// 2. OTP ellenőrzés és JWT adás
function verifyOTP(username, otp) {
  if (otpStore[username] && otpStore[username] === otp) {
    delete otpStore[username];
    const user = users.find((u) => u.username === username);

    const token = jwt.sign(
      { id: user.id, username: user.username, roles: user.roles },
      crypto.randomBytes(128),
      { expiresIn: "1h" }
    );
    return { message: "Sikeres bejelentkezés!", token };
  }
  return { error: "Hibás vagy lejárt OTP kód!" };
}

// Console test
// readline interface létrehozása
/*
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});
// Első lépés: username + password bekérése 
rl.question("Felhasználónév: ", (username) => {
  rl.question("Jelszó: ", (password) => {
    const loginResult = requestLogin(username, password);
    console.log(loginResult.message || loginResult.error);

    if (loginResult.error) {
      rl.close();
      return;
    }
    console.log(otpStore)
    // Második lépés: OTP bekérése
    rl.question("Add meg az e-mailben kapott OTP-t ", (otp) => {
      const verifyResult = verifyOTP(username, otp);
      console.log(verifyResult);
      rl.close();
    });
  });
});
*/
let OTP_Username = ""
app.post("/login", (req, res) =>{
  const { username, password } = req.body;
  requestLogin(username, password);
  const loginResult = requestLogin(username, password);
    console.log(loginResult.message || loginResult.error);
  if (loginResult.error) {
    return res.status(401).json({ error: loginResult.error });
  }
    OTP_Username = username
    console.log("OTP store:", otpStore);
    // OTP bekérése
    return res.status(200).json({ message: "Sikeres bejelentkezés, kérlek add meg az OTP-t!" });
});
app.post("/OTP", (req, res) => {
  const OTP  = req.body;
  const verifyResult = verifyOTP(OTP_Username, OTP);

  if (verifyResult.error) {
    return res.status(401).json({ error: verifyResult.error });
  }

  return res.status(200).json({ message: "Sikeres OTP ellenőrzés!" });
});

const PORT = 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));