import { useRef, useEffect, useCallback } from 'react';
import { useMatrix } from '../contexts/MatrixContext';
import { useMatrixRain } from '../hooks/useMatrixRain';
import type { MousePos, FpsTracker, GlitchState } from '../types';

export default function MatrixCanvas() {
  const { state, themeConfig, setStats } = useMatrix();
  const bgRef = useRef<HTMLCanvasElement>(null);
  const fgRef = useRef<HTMLCanvasElement>(null);
  const mouseRef = useRef<MousePos>({ x: window.innerWidth / 2, y: window.innerHeight / 2 });

  const {
    columnsRef, userColumnsRef, ripplesRef, timeRef, addRipple,
  } = useMatrixRain();

  // Keep latest values in refs for the stable animation loop
  const stateRef = useRef(state);
  stateRef.current = state;
  const themeRef = useRef(themeConfig());
  themeRef.current = themeConfig();
  const setStatsRef = useRef(setStats);
  setStatsRef.current = setStats;

  const fpsRef = useRef<FpsTracker>({ count: 0, time: 0, value: 0 });
  const glitchRef = useRef<GlitchState>({ active: false, timer: 0, x: 0, w: 0 });
  const rafRef = useRef<number>(0);

  // Resize canvases
  useEffect(() => {
    const resize = () => {
      const dpr = window.devicePixelRatio || 1;
      [bgRef, fgRef].forEach(r => {
        const c = r.current;
        if (!c) return;
        c.width = window.innerWidth * dpr;
        c.height = window.innerHeight * dpr;
        c.style.width = window.innerWidth + 'px';
        c.style.height = window.innerHeight + 'px';
        c.getContext('2d')!.setTransform(dpr, 0, 0, dpr, 0, 0);
      });
    };
    resize();
    window.addEventListener('resize', resize);
    return () => window.removeEventListener('resize', resize);
  }, []);

  // Mouse tracking
  useEffect(() => {
    const move = (e: MouseEvent) => { mouseRef.current = { x: e.clientX, y: e.clientY }; };
    const touch = (e: TouchEvent) => { mouseRef.current = { x: e.touches[0].clientX, y: e.touches[0].clientY }; };
    window.addEventListener('mousemove', move);
    window.addEventListener('touchmove', touch, { passive: true });
    return () => { window.removeEventListener('mousemove', move); window.removeEventListener('touchmove', touch); };
  }, []);

  const handleClick = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    addRipple(e.clientX, e.clientY);
  }, [addRipple]);

  // Stable animation loop — empty deps so it only starts once
  useEffect(() => {
    let lastTime = performance.now();

    const loop = (now: number) => {
      const dt = Math.min((now - lastTime) / 1000, 0.05);
      lastTime = now;

      const s = stateRef.current;
      const theme = themeRef.current;

      // FPS counter
      fpsRef.current.count++;
      fpsRef.current.time += dt;
      if (fpsRef.current.time >= 1) {
        const fps = fpsRef.current.count;
        fpsRef.current.count = 0;
        fpsRef.current.time = 0;
        fpsRef.current.value = fps;
        const totalChars = columnsRef.current.reduce((sum, c) => sum + c.chars.length, 0)
          + userColumnsRef.current.reduce((sum, c) => sum + c.chars.length, 0);
        setStatsRef.current({ fps, columns: columnsRef.current.length, chars: totalChars });
      }

      // Update entities (mode doesn't matter for update, columns manage their own reset)
      columnsRef.current.forEach(c => c.update(dt));
      userColumnsRef.current.forEach(c => c.update(dt));
      ripplesRef.current.forEach(r => r.update(dt));
      ripplesRef.current = ripplesRef.current.filter(r => r.alive);

      // Glitch logic
      if (s.glitchEnabled) {
        const g = glitchRef.current;
        g.timer -= dt;
        if (g.timer <= 0 && !g.active && Math.random() < 0.008) {
          g.active = true;
          g.timer = 0.04 + Math.random() * 0.08;
          g.x = Math.random() * window.innerWidth;
          g.w = 30 + Math.random() * 100;
        }
        if (g.active && g.timer <= 0) {
          g.active = false;
          g.timer = 2 + Math.random() * 4;
        }
      }

      // Draw background (fade trail)
      const bgCtx = bgRef.current?.getContext('2d');
      if (bgCtx) {
        bgCtx.fillStyle = `rgba(0, 0, 0, ${s.mode === 'dense' ? 0.08 : 0.12})`;
        bgCtx.fillRect(0, 0, window.innerWidth, window.innerHeight);
        columnsRef.current.forEach(c => c.draw(bgCtx, theme));
      }

      // Draw foreground
      const fgCtx = fgRef.current?.getContext('2d');
      if (fgCtx) {
        fgCtx.clearRect(0, 0, window.innerWidth, window.innerHeight);
        userColumnsRef.current.forEach(c => c.draw(fgCtx, theme));
        ripplesRef.current.forEach(r => r.draw(fgCtx, theme));

        // Mouse glow
        const { x: mx, y: my } = mouseRef.current;
        const grad = fgCtx.createRadialGradient(mx, my, 0, mx, my, 80);
        grad.addColorStop(0, theme.primary + '14');
        grad.addColorStop(0.5, theme.primary + '08');
        grad.addColorStop(1, 'transparent');
        fgCtx.fillStyle = grad;
        fgCtx.fillRect(mx - 80, my - 80, 160, 160);

        // Glitch render
        if (s.glitchEnabled && glitchRef.current.active) {
          const dpr = window.devicePixelRatio || 1;
          const g = glitchRef.current;
          try {
            const imgData = fgCtx.getImageData(
              g.x * dpr, 0,
              Math.min(g.w * dpr, Math.max(1, (window.innerWidth - g.x) * dpr)),
              window.innerHeight * dpr,
            );
            fgCtx.putImageData(imgData, g.x * dpr + (Math.random() - 0.5) * 15 * dpr, Math.random() * 3 * dpr);
          } catch { /* ignore */ }
        }
      }

      rafRef.current = requestAnimationFrame(loop);
    };

    rafRef.current = requestAnimationFrame(loop);
    return () => cancelAnimationFrame(rafRef.current);
  }, []);

  return (
    <>
      <canvas ref={bgRef} style={{ position: 'fixed', top: 0, left: 0, zIndex: 1 }} />
      <canvas ref={fgRef} style={{ position: 'fixed', top: 0, left: 0, zIndex: 2 }} onClick={handleClick} />
    </>
  );
}
