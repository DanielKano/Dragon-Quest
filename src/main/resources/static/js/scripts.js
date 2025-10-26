/* ========== Modular JS: init sequence ========== */
(function(){
  'use strict';

  /* ----------------- Partículas ----------------- */
  function initParticles(){
    // simple canvas-based particles for performance
    const container = document.getElementById('particles');
    const canvas = document.createElement('canvas');
    canvas.style.position='absolute';
    canvas.style.inset='0';
    canvas.style.width='100%';
    canvas.style.height='100%';
    canvas.width = innerWidth * devicePixelRatio;
    canvas.height = innerHeight * devicePixelRatio;
    canvas.style.zIndex = 0;
    const ctx = canvas.getContext('2d');
    container.appendChild(canvas);

    let particles = [];
    const count = Math.max(18, Math.min(45, Math.floor(innerWidth/40)));

    for(let i=0;i<count;i++){
      particles.push({
        x: Math.random()*canvas.width,
        y: Math.random()*canvas.height,
        r: (Math.random()*3+1.2)*devicePixelRatio,
        vx: (Math.random()*0.6-0.3)*devicePixelRatio,
        vy: - (Math.random()*0.2+0.15)*devicePixelRatio,
        alpha: Math.random()*0.45+0.05,
        sway: Math.random()*0.6+0.2,
        phase: Math.random()*Math.PI*2
      });
    }

    let last = performance.now();
    function loop(now){
      const dt = Math.min(60,(now-last));
      last = now;
      ctx.clearRect(0,0,canvas.width,canvas.height);
      particles.forEach(p=>{
        p.x += p.vx * dt * 0.06;
        p.y += p.vy * dt * 0.06;
        p.phase += 0.01 * dt;
        // sway
        const sway = Math.sin(p.phase) * p.sway * devicePixelRatio;
        const gx = p.x + sway;
        // draw
        ctx.beginPath();
        ctx.fillStyle = `rgba(224,179,58,${p.alpha})`;
        ctx.arc(gx, p.y, p.r, 0, Math.PI*2);
        ctx.fill();
        // reset
        if(p.y + p.r < -20 || p.x < -80 || p.x > canvas.width+80){
          p.x = Math.random()*canvas.width;
          p.y = canvas.height + 30 + Math.random()*80;
        }
      });
      if(!window.matchMedia('(prefers-reduced-motion: reduce)').matches) requestAnimationFrame(loop);
    }
    requestAnimationFrame(loop);

    // handle resize
    window.addEventListener('resize', () => {
      canvas.width = innerWidth * devicePixelRatio;
      canvas.height = innerHeight * devicePixelRatio;
    });
  }

  /* ----------------- Magic cursor ----------------- */
  function initMagicCursor(){
    const cursor = document.getElementById('magicCursor');
    let visible = false;
    document.addEventListener('mousemove', e => {
      if(window.matchMedia('(max-width: 480px)').matches) return; // mobile: hide
      const x = e.clientX, y = e.clientY;
      cursor.style.left = x + 'px';
      cursor.style.top = y + 'px';
      cursor.style.opacity = 1;
      visible = true;
      cursor.style.transform = 'translate(-50%,-50%) scale(1)';
      clearTimeout(cursor._hideTO);
      cursor._hideTO = setTimeout(()=>{ cursor.style.opacity = 0; }, 1200);
    });

    // grow over interactive elements
    ['a','button','.btn','.rank','.scroll-card'].forEach(selector=>{
      document.addEventListener('mouseover', e=>{
        if(e.target.closest && e.target.closest(selector)){
          cursor.style.transform = 'translate(-50%,-50%) scale(1.7)';
        }
      });
      document.addEventListener('mouseout', e=>{
        cursor.style.transform = 'translate(-50%,-50%) scale(1)';
      });
    });
  }

  /* ----------------- Scroll cards (pergaminos) ----------------- */
  function initScrolls(){
    const cards = Array.from(document.querySelectorAll('.scroll-card'));
    const sfx = document.getElementById('scrollSfx');
    cards.forEach(card=>{
      function toggle(openedByKey=false){
        const isOpen = card.classList.contains('open');
        // close all
        document.querySelectorAll('.scroll-card.open').forEach(c=>{
          if(c !== card) {
            c.classList.remove('open');
            c.setAttribute('aria-expanded','false');
          }
        });

        if(!isOpen){
          card.classList.add('open');
          card.setAttribute('aria-expanded','true');
          // play sound
          try { sfx.currentTime = 0; sfx.play().catch(()=>{}); } catch(e){}
          // small visual "unwrap" via CSS already
        } else {
          card.classList.remove('open');
          card.setAttribute('aria-expanded','false');
        }
      }

      card.addEventListener('click', () => toggle());
      // keyboard accessibility
      card.addEventListener('keydown', (ev) => {
        if(ev.key === 'Enter' || ev.key === ' '){
          ev.preventDefault();
          toggle(true);
        }
      });
    });
  }

  /* ----------------- Stats counters ----------------- */
  function initStats(){
    const nodes = document.querySelectorAll('.num');
    const cfg = {threshold:0.5};
    const observer = new IntersectionObserver(entries=>{
      entries.forEach(entry=>{
        if(entry.isIntersecting){
          const el = entry.target;
          const target = parseInt(el.getAttribute('data-target')) || 0;
          animateCount(el, target);
          observer.unobserve(el);
        }
      });
    }, cfg);
    nodes.forEach(n=>observer.observe(n));

    function animateCount(el, target){
      let current = 0;
      const duration = 1400;
      const start = performance.now();
      const step = (now) => {
        const t = Math.min(1, (now - start) / duration);
        const eased = (1 - Math.cos(t * Math.PI)) / 2; // easeOut
        el.textContent = Math.floor(current + (target - current) * eased).toLocaleString();
        if(t < 1) requestAnimationFrame(step);
      };
      requestAnimationFrame(step);
    }
  }

  /* ----------------- Ranks modal ----------------- */
  function initRanks(){
    const rankInfo = {
      'F':{
        title:'Novicio',
        desc:'El camino comienza aquí. Como Novicio aprenderás los fundamentos...',
        perks:['Acceso a misiones F','Tutoriales','Equipamiento básico','Grupos de entrenamiento'],
        next:'E - Explorador'
      },
      'E':{
        title:'Explorador',
        desc:'Te aventuras más allá y enfrentas peligros reales...',
        perks:['Misiones de exploración','Armas rango E','Recompensas','Clasificaciones'],
        next:'D - Guerrero'
      },
      'D':{
        title:'Guerrero',
        desc:'Tu nombre comienza a sonar. Enfrentas criaturas peligrosas...',
        perks:['Misiones de combate','Armamento','Entrenamiento avanzado','Reconocimiento'],
        next:'C - Paladín'
      },
      'C':{
        title:'Paladín',
        desc:'Eres un pilar del Gremio...',
        perks:['Misiones épicas','Armaduras','Acceso VIP','Mentorías'],
        next:'B - Campeón'
      },
      'B':{
        title:'Campeón',
        desc:'Hazañas legendarias...',
        perks:['Cacería de dragones','Equipamiento maestro','Consejo','Bibliotecas secretas'],
        next:'A - Élite'
      },
      'A':{
        title:'Élite',
        desc:'Responsabilidad y poder...',
        perks:['Misiones dimensionales','Artefactos','Audiencias','Liderazgo'],
        next:'S - Leyenda'
      },
      'S':{
        title:'Leyenda',
        desc:'Tu legado trasciende el tiempo...',
        perks:['Misiones que definen eras','Estatua','Crónicas','Poderes únicos'],
        next:'¡Eres una Leyenda!'
      }
    };

    const ranks = document.querySelectorAll('.rank');
    const modal = document.getElementById('rankModal');
    const modalBody = document.getElementById('rankModalBody');
    const closeBtn = document.getElementById('closeModal');
    const unlockSfx = document.getElementById('rankSfx');

    function open(rankKey){
      const info = rankInfo[rankKey];
      if(!info) return;
      modalBody.innerHTML = `
        <h2 id="rankModalTitle">${info.title}</h2>
        <p style="color:#3d2f2f">${info.desc}</p>
        <h4 style="color:#6a3b12;margin-top:12px">Privilegios</h4>
        <ul style="color:#2b1f14">
          ${info.perks.map(p=>`<li>• ${p}</li>`).join('')}
        </ul>
        <div style="margin-top:12px;padding:10px;border-left:4px solid #b07f15;background:rgba(176,127,21,0.06)">
          <strong>Siguiente:</strong> ${info.next}
        </div>
      `;
      modal.classList.add('show');
      modal.setAttribute('aria-hidden','false');
      try{ unlockSfx.currentTime=0; unlockSfx.play().catch(()=>{}); }catch(e){}
    }

    function close(){
      modal.classList.remove('show');
      modal.setAttribute('aria-hidden','true');
    }

    ranks.forEach(r=>{
      r.addEventListener('click', ()=> open(r.dataset.rank));
      r.addEventListener('keydown', e => { if(e.key==='Enter' || e.key===' ') open(r.dataset.rank); });
    });

    closeBtn.addEventListener('click', close);
    modal.addEventListener('click', (e)=>{ if(e.target === modal) close(); });
    document.addEventListener('keydown', e=>{ if(e.key==='Escape') close(); });
  }

  /* ----------------- Audio control ----------------- */
  function initAudio(){
    const amb = document.getElementById('ambienceAudio');
    const toggle = document.getElementById('soundToggle');
    let enabled = false;

    // restore preference
    try{
      const pref = localStorage.getItem('dq_amb_on');
      if(pref === '1'){ enabled = true; amb.volume = 0.55; amb.play().catch(()=>{}); toggle.setAttribute('aria-pressed','true'); toggle.style.opacity=1; }
    }catch(e){}

    toggle.addEventListener('click', ()=>{
      enabled = !enabled;
      toggle.setAttribute('aria-pressed', String(enabled));
      if(enabled){
        amb.volume = 0.55;
        amb.play().catch(()=>{});
        localStorage.setItem('dq_amb_on','1');
      } else {
        amb.pause();
        localStorage.setItem('dq_amb_on','0');
      }
    });
  }

  /* ----------------- Init all ----------------- */
  function initAll(){
    if(!window.matchMedia('(prefers-reduced-motion: reduce)').matches){
      initParticles();
    }
    initMagicCursor();
    initScrolls();
    initStats();
    initRanks();
    initAudio();
  }

  // Run when DOM is ready
  if(document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', initAll);
  } else initAll();

})();