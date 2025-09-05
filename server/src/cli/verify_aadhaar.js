const fs=require('fs')
const StreamZip=require('node-stream-zip')
const {XMLParser}=require('fast-xml-parser')
async function run(){
  const file=process.argv[2]
  const pwd=process.argv[3]||''
  if(!file||!fs.existsSync(file)){ console.error('file_not_found'); process.exit(2) }
  const zip=new StreamZip.async({file,password:pwd})
  const entries=await zip.entries()
  let xmlName=null
  for(const name in entries){ if(name.toLowerCase().endsWith('.xml')){ xmlName=name; break } }
  if(!xmlName){ console.error('xml_missing'); process.exit(3) }
  const data=await zip.entryData(xmlName)
  await zip.close()
  const xml=new XMLParser({ignoreAttributes:false,attributeNamePrefix:''}).parse(data.toString('utf8'))
  let root=xml.UidData||xml.uidData||xml
  const out={}
  if(root){
    if(root.name) out.name=root.name
    if(root.uid) out.uid=root.uid
    if(root.gender) out.gender=root.gender
    if(root.dob) out.dob=root.dob
    if(root.referenceId) out.referenceId=root.referenceId
  }
  if(Object.keys(out).length===0){
    const s=data.toString('utf8')
    const mName=s.match(/name\s*=\s*"([^"]+)"/i); if(mName) out.name=mName[1]
    const mUid=s.match(/uid\s*=\s*"([^"]+)"/i); if(mUid) out.uid=mUid[1]
    const mGen=s.match(/gender\s*=\s*"([^"]+)"/i); if(mGen) out.gender=mGen[1]
    const mDob=s.match(/dob\s*=\s*"([^"]+)"/i); if(mDob) out.dob=mDob[1]
    const mRef=s.match(/referenceId\s*=\s*"([^"]+)"/i); if(mRef) out.referenceId=mRef[1]
  }
  console.log(JSON.stringify(out))
}
run().catch(e=>{ console.error('error'); process.exit(1) })

