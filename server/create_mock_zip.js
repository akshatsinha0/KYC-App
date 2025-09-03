const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const xmlPath = path.join(__dirname, 'src/web/sample_aadhaar.xml');
const zipPath = path.join(__dirname, 'src/web/mock_aadhaar.zip');

const shareCode = '1234';

console.log('Creating mock Aadhaar ZIP file...');
console.log('Share code:', shareCode);

try {
  try {
    execSync(`7z a -p${shareCode} "${zipPath}" "${xmlPath}"`, { stdio: 'inherit' });
  } catch (e) {
    const psCommand = `Compress-Archive -Path "${xmlPath}" -DestinationPath "${zipPath}" -Force`;
    execSync(`powershell -Command "${psCommand}"`, { stdio: 'inherit' });
    console.log('Note: Created without password protection. Use share code 1234 anyway for testing.');
  }
  
  console.log('✅ Mock ZIP created at:', zipPath);
  console.log('📁 Use this file in the web demo');
  console.log('🔑 Share code: 1234');
  
} catch (error) {
  console.error('❌ Error creating ZIP:', error.message);
  console.log('\nManual steps:');
  console.log('1. Create a ZIP file manually');
  console.log('2. Add the sample_aadhaar.xml file to it');
  console.log('3. Set password to "1234"');
  console.log('4. Save as mock_aadhaar.zip in src/web/');
}