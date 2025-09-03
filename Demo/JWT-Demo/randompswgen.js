const fs = require('fs');
const crypto = require('crypto');

const bytes = crypto.randomBytes(100_000_000); // 100 MB random adat
fs.writeFileSync('Secret.env', bytes); // nyers bináris fájl

console.log('Fájl létrehozva: Secret.env');
