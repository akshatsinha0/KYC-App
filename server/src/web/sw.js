const CACHE_NAME='bharat-kyc-web-v1';
const ASSETS=[
  '/web/',
  '/web/index.html',
  '/web/digilocker.html',
  '/web/queue.html',
  '/web/aadhaar.html',
  '/web/assistant.html',
  '/web/face.html',
  '/web/js/zip.min.js'
];
self.addEventListener('install',e=>{
  e.waitUntil(caches.open(CACHE_NAME).then(c=>c.addAll(ASSETS)).then(()=>self.skipWaiting()));
});
self.addEventListener('activate',e=>{
  e.waitUntil(caches.keys().then(keys=>Promise.all(keys.map(k=>k===CACHE_NAME?null:caches.delete(k)))));
});
self.addEventListener('fetch',e=>{
  const url=new URL(e.request.url);
  if(url.pathname.startsWith('/web/')){
    e.respondWith(
      caches.match(e.request).then(cached=>cached||fetch(e.request).then(res=>{
        const copy=res.clone(); caches.open(CACHE_NAME).then(c=>c.put(e.request,copy)); return res;
      }).catch(()=>cached))
    );
  }
});

