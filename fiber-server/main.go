package main

import (
	"encoding/json"
	"os"
	"sync"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/recover"
	"github.com/google/uuid"
)

type KycSession struct{
	ID string `json:"id"`
	Status string `json:"status"`
	Method string `json:"method"`
	CreatedAt time.Time `json:"createdAt"`
	UpdatedAt time.Time `json:"updatedAt"`
	ResultDigest *string `json:"resultDigest,omitempty"`
}

type EventsResult struct{ Ok bool `json:"ok"`; Count int `json:"count"`; Duplicate bool `json:"duplicate"` }

type store struct{
	mu sync.RWMutex
	sessions map[string]*KycSession
	idemCreate map[string]*KycSession
	idemFinalize map[string]*KycSession
	idemEvents map[string]EventsResult
}

func newStore()*store{
	return &store{
		sessions:make(map[string]*KycSession),
		idemCreate:make(map[string]*KycSession),
		idemFinalize:make(map[string]*KycSession),
		idemEvents:make(map[string]EventsResult),
	}
}

func main(){
	app:=fiber.New()
	app.Use(logger.New(),recover.New())
	s:=newStore()

	app.Get("/healthz", func(c *fiber.Ctx) error { return c.SendString("ok") })

	app.Post("/v1/kyc/sessions", func(c *fiber.Ctx) error{
		idem:=string(c.Request().Header.Peek("Idempotency-Key"))
		s.mu.Lock(); defer s.mu.Unlock()
		if idem!=""{ if prev,ok:=s.idemCreate[idem]; ok { return c.Status(201).JSON(prev) } }
		var body struct{ Method string `json:"method"` }
		_ = c.BodyParser(&body); if body.Method==""{ body.Method="unknown" }
		now:=time.Now().UTC()
		sess:=&KycSession{ ID:uuid.New().String(), Status:"created", Method:body.Method, CreatedAt:now, UpdatedAt:now }
		s.sessions[sess.ID]=sess
		if idem!=""{ s.idemCreate[idem]=sess }
		return c.Status(201).JSON(sess)
	})

	app.Get("/v1/kyc/sessions/:id", func(c *fiber.Ctx) error{
		id:=c.Params("id")
		s.mu.RLock(); defer s.mu.RUnlock()
		if sess,ok:=s.sessions[id]; ok { return c.JSON(sess) }
		return c.Status(404).JSON(fiber.Map{"error":"not_found"})
	})

	app.Post("/v1/kyc/sessions/:id/finalize", func(c *fiber.Ctx) error{
		id:=c.Params("id")
		s.mu.Lock(); defer s.mu.Unlock()
		sess,ok:=s.sessions[id]; if !ok { return c.Status(404).JSON(fiber.Map{"error":"not_found"}) }
		idem:=string(c.Request().Header.Peek("Idempotency-Key"))
		scopeKey:=id+"|"+idem
		if idem!=""{ if prev,ok:=s.idemFinalize[scopeKey]; ok { return c.JSON(prev) } }
		var body struct{ ResultDigest string `json:"resultDigest"`; Signature string `json:"signature"`; DeviceId string `json:"deviceId"` }
		if err:=c.BodyParser(&body); err!=nil { return c.Status(400).JSON(fiber.Map{"error":"invalid_json"}) }
		if body.ResultDigest==""||body.Signature==""||body.DeviceId=="" { return c.Status(400).JSON(fiber.Map{"error":"invalid_request"}) }
		sess.Status="finalized"; sess.UpdatedAt=time.Now().UTC(); sess.ResultDigest=&body.ResultDigest
		if idem!=""{ s.idemFinalize[scopeKey]=sess }
		return c.JSON(sess)
	})

	app.Post("/v1/events", func(c *fiber.Ctx) error{
		dem:=string(c.Request().Header.Peek("Idempotency-Key"))
		s.mu.Lock(); defer s.mu.Unlock()
		if dem!=""{ if prev,ok:=s.idemEvents[dem]; ok { prev.Duplicate=true; return c.Status(202).JSON(prev) } }
		body:=c.Body()
		var arr []map[string]any
		var one map[string]any
		count:=0
		if err:=json.Unmarshal(body,&arr); err==nil && len(arr)>0 { count=len(arr) } else if err2:=json.Unmarshal(body,&one); err2==nil && len(one)>0 { count=1 }
		res:=EventsResult{Ok:true,Count:count,Duplicate:false}
		if dem!=""{ s.idemEvents[dem]=res }
		return c.Status(202).JSON(res)
	})

	app.Post("/v1/digilocker/callback", func(c *fiber.Ctx) error{
		code:=c.Query("code")
		state:=c.Query("state")
		if code==""||state==""{ return c.Status(400).SendString("<h3>Missing code/state</h3>") }
		html:="<!doctype html><html><body><h1>DigiLocker callback received</h1><pre>code="+code+"\nstate="+state+"</pre></body></html>"
		return c.Type("html").SendString(html)
	})

	port:=os.Getenv("PORT"); if port==""{ port=":8080" } else { port=":"+port }
	_ = app.Listen(port)
}

