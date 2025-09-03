const express=require('express')
const cors=require('cors')
const bodyParser=require('body-parser')
const morgan=require('morgan')
const {v4:uuidv4}=require('uuid')
const path=require('path')

const app=express()
app.use(cors())
app.use(bodyParser.json({limit:'1mb'}))
app.use(morgan('dev'))

const PORT=process.env.PORT||8080

const sessionsById=new Map()
const idempCreateSession=new Map()
const idempFinalize=new Map()
const events=[]

app.use('/web',express.static(path.join(__dirname,'web')))

app.post('/v1/kyc/sessions',(req,res)=>{
  const idem=req.get('Idempotency-Key')||''
  if(idem&&idempCreateSession.has(idem))return res.status(201).json(idempCreateSession.get(idem))
  const now=new Date().toISOString()
  const id=uuidv4()
  const method=req.body?.method||'unknown'
  const session={id,status:'created',method,createdAt:now,updatedAt:now}
  sessionsById.set(id,session)
  if(idem)idempCreateSession.set(idem,session)
  return res.status(201).json(session)
})

app.get('/v1/kyc/sessions/:id',(req,res)=>{
  const s=sessionsById.get(req.params.id)
  if(!s)return res.status(404).json({error:'not_found'})
  return res.json(s)
})

app.post('/v1/kyc/sessions/:id/finalize',(req,res)=>{
  const idem=req.get('Idempotency-Key')||''
  const key=`${req.params.id}:${idem}`
  const s=sessionsById.get(req.params.id)
  if(!s)return res.status(404).json({error:'not_found'})
  if(idem&&idempFinalize.has(key))return res.json(idempFinalize.get(key))
  const {resultDigest,signature,deviceId}=req.body||{}
  if(!resultDigest||!signature||!deviceId)return res.status(400).json({error:'invalid_request'})
  s.status='finalized'
  s.updatedAt=new Date().toISOString()
  s.resultDigest=resultDigest
  sessionsById.set(s.id,s)
  if(idem)idempFinalize.set(key,s)
  return res.json(s)
})

app.all('/v1/digilocker/callback',(req,res)=>{
  const code=(req.query.code||req.body?.code||'').toString()
  const state=(req.query.state||req.body?.state||'').toString()
  if(!code||!state){
    return res.status(400).send('<html><body><h3>Missing code/state</h3></body></html>')
  }
  const s=sessionsById.get(state)
  if(s){s.status='digilocker_code_received';s.updatedAt=new Date().toISOString()}
  return res.status(200).send(`<!doctype html><html><body>
  <h3>DigiLocker callback received</h3>
  <p>code=${code}</p>
  <p>state=${state}</p>
  <p>This is a local stub. Swap this endpoint with your on-prem server when ready.</p>
  </body></html>`)
})

app.post('/v1/events',(req,res)=>{
  const idem=req.get('Idempotency-Key')||''
  if(idem&&events.find(e=>e.idem===idem))return res.status(202).json({ok:true,count:0,duplicate:true})
  const evts=Array.isArray(req.body)?req.body:[req.body]
  evts.forEach(e=>events.push({ts:Date.now(),idem,evt:e}))
  return res.status(202).json({ok:true,count:evts.length,duplicate:false})
})

app.get('/healthz',(_req,res)=>res.json({ok:true}))

app.listen(PORT,()=>{
  console.log(`bharat-kyc local stub listening on http://localhost:${PORT}`)
  console.log('Open http://localhost:'+PORT+'/web/digilocker.html to test redirect/callback')
  console.log('Open http://localhost:'+PORT+'/web/queue.html to test session + finalize locally')
  console.log('Open http://localhost:'+PORT+'/web/aadhaar.html for Aadhaar ZIP verifier (password-protected supported)')
})

