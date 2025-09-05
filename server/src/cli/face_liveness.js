const seq=(process.argv[2]||'').split(',').map(x=>Number(x)).filter(x=>!isNaN(x))
if(!seq.length){ console.log(JSON.stringify({error:'bad_seq'})); process.exit(1) }
const min=Math.min(...seq)
const max=Math.max(...seq)
const blink=min<0.18 && (max-min)>0.12
const score=blink?0.6:0
console.log(JSON.stringify({ok:score>=0.6,score}))
