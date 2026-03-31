import { useRef, useCallback, useEffect } from 'react';
import { randomChar, randomInt, type PoolKey } from '../utils/chars';
import { useMatrix } from '../contexts/MatrixContext';
import { prepareWithSegments, layoutWithLines, clearCache } from '@chenglou/pretext';
import type { ModeConfig, Theme } from '../types';

// ========== Column ==========
class Column {
  x: number;
  fontSize: number;
  speed: number;
  maxLength: number;
  brightness: number;
  chars: string[];
  y: number;
  changeTimer: number;
  paused: boolean;
  pauseTimer: number;
  pools: PoolKey[];
  modeConfig: ModeConfig;

  constructor(x: number, fontSize: number, modeConfig: ModeConfig, pools: PoolKey[]) {
    this.x = x;
    this.fontSize = fontSize;
    this.speed = 0;
    this.maxLength = 0;
    this.brightness = 0;
    this.chars = [];
    this.y = 0;
    this.changeTimer = 0;
    this.paused = false;
    this.pauseTimer = 0;
    this.pools = pools;
    this.modeConfig = modeConfig;
    this.reset(true);
  }

  reset(initial = false): void {
    const [minS, maxS] = this.modeConfig.speed;
    const [minL, maxL] = this.modeConfig.length;
    const [minB, maxB] = this.modeConfig.brightness;
    this.speed = minS + Math.random() * (maxS - minS);
    this.maxLength = randomInt(minL, maxL);
    this.brightness = minB + Math.random() * maxB;
    this.chars = Array.from({ length: this.maxLength }, () => randomChar(this.pools));
    this.y = initial
      ? -Math.random() * window.innerHeight * 2
      : -this.maxLength * this.fontSize - Math.random() * window.innerHeight;
    this.changeTimer = Math.random() * 100;
    this.paused = false;
    this.pauseTimer = 0;
  }

  update(dt: number): void {
    this.changeTimer += dt;
    if (this.changeTimer > 0.05) {
      this.changeTimer = 0;
      if (Math.random() < 0.25) {
        const idx = randomInt(0, this.chars.length - 1);
        this.chars[idx] = randomChar(this.pools);
      }
    }

    if (this.paused) {
      this.pauseTimer -= dt;
      if (this.pauseTimer <= 0) this.paused = false;
      return;
    }
    if (Math.random() < 0.002) {
      this.paused = true;
      this.pauseTimer = 0.2 + Math.random() * 0.4;
    }

    this.y += this.speed * this.fontSize * dt;
    if (this.y - this.maxLength * this.fontSize > window.innerHeight) {
      this.reset();
    }
  }

  draw(ctx: CanvasRenderingContext2D, theme: Theme): void {
    const { primary, secondary, head } = theme;
    ctx.font = `${this.fontSize}px "PingFang SC","Microsoft YaHei",monospace`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    const H = window.innerHeight;
    for (let i = 0; i < this.chars.length; i++) {
      const cy = this.y - i * this.fontSize;
      if (cy < -this.fontSize || cy > H + this.fontSize) continue;

      const fadeRatio = i / this.chars.length;
      const isHead = i === 0;
      const isNearHead = i <= 2;

      if (isHead) {
        ctx.save();
        ctx.shadowColor = primary;
        ctx.shadowBlur = 25;
        ctx.fillStyle = head;
        ctx.globalAlpha = 1;
        ctx.fillText(this.chars[i], this.x, cy);
        ctx.shadowBlur = 15;
        ctx.fillText(this.chars[i], this.x, cy);
        ctx.restore();
      } else if (isNearHead) {
        ctx.save();
        ctx.shadowColor = primary;
        ctx.shadowBlur = 10;
        ctx.globalAlpha = (0.95 - fadeRatio * 0.3) * this.brightness;
        ctx.fillStyle = primary;
        ctx.fillText(this.chars[i], this.x, cy);
        ctx.restore();
      } else {
        const alpha = Math.max(0, (1 - fadeRatio) * 0.8) * this.brightness;
        if (alpha < 0.02) continue;
        ctx.save();
        ctx.globalAlpha = alpha;
        ctx.fillStyle = secondary;
        if (alpha > 0.25) {
          ctx.shadowColor = primary;
          ctx.shadowBlur = 3;
        }
        ctx.fillText(this.chars[i], this.x, cy);
        ctx.restore();
      }
    }
  }
}

// ========== UserColumn ==========
interface TrailPoint {
  y: number;
}

class UserColumn {
  originalText: string;
  chars: string[];
  x: number;
  y: number;
  speed: number;
  fontSize: number;
  trail: TrailPoint[];
  trailLength: number;
  changeTimer: number;
  pools: PoolKey[];

  constructor(text: string, x: number, delay: number, fontSize: number, pools: PoolKey[]) {
    this.originalText = text;
    this.chars = [...text];
    this.x = x;
    this.y = -(delay * fontSize * 3);
    this.speed = 1.5 + Math.random() * 2.5;
    this.fontSize = fontSize + 4;
    this.trail = [];
    this.trailLength = randomInt(5, 10);
    this.changeTimer = 0;
    this.pools = pools;
  }

  update(dt: number): void {
    this.changeTimer += dt;
    if (this.changeTimer > 0.12) {
      this.changeTimer = 0;
      if (Math.random() < 0.12) {
        const idx = randomInt(0, this.chars.length - 1);
        this.chars[idx] = randomChar(this.pools);
        setTimeout(() => {
          this.chars[idx] = this.originalText[idx] || randomChar(this.pools);
        }, 60 + Math.random() * 120);
      }
    }

    this.y += this.speed * this.fontSize * dt;
    this.trail.unshift({ y: this.y });
    if (this.trail.length > this.trailLength) this.trail.pop();

    if (this.y > window.innerHeight + this.chars.length * this.fontSize + 200) {
      this.y = -this.chars.length * this.fontSize * 2 - Math.random() * window.innerHeight * 0.5;
      this.trail = [];
    }
  }

  draw(ctx: CanvasRenderingContext2D, theme: Theme): void {
    const { primary, head } = theme;
    ctx.font = `bold ${this.fontSize}px "PingFang SC","Microsoft YaHei",monospace`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    const H = window.innerHeight;
    const spacing = this.fontSize * 1.3;

    // Trail ghosts
    for (let t = 0; t < this.trail.length; t++) {
      const ty = this.trail[t].y;
      const alpha = (1 - t / this.trail.length) * 0.12;
      for (let i = 0; i < this.chars.length; i++) {
        const cy = ty - i * spacing;
        if (cy < -this.fontSize || cy > H + this.fontSize) continue;
        ctx.save();
        ctx.globalAlpha = alpha;
        ctx.fillStyle = primary;
        ctx.fillText(this.chars[i], this.x, cy);
        ctx.restore();
      }
    }

    // Main text
    for (let i = 0; i < this.chars.length; i++) {
      const cy = this.y - i * spacing;
      if (cy < -this.fontSize || cy > H + this.fontSize) continue;

      const isHead = i === 0;
      const fadeRatio = i / this.chars.length;

      if (isHead) {
        ctx.save();
        ctx.shadowColor = primary;
        ctx.shadowBlur = 35;
        ctx.fillStyle = head;
        ctx.globalAlpha = 1;
        ctx.fillText(this.chars[i], this.x, cy);
        ctx.fillText(this.chars[i], this.x, cy);
        ctx.restore();
      } else {
        const alpha = 1 - fadeRatio * 0.5;
        ctx.save();
        ctx.globalAlpha = alpha;
        ctx.shadowColor = primary;
        ctx.shadowBlur = 8;
        ctx.fillStyle = primary;
        ctx.fillText(this.chars[i], this.x, cy);
        ctx.restore();
      }
    }
  }
}

// ========== Ripple ==========
class Ripple {
  x: number;
  y: number;
  radius: number;
  maxRadius: number;
  speed: number;
  alive: boolean;

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
    this.radius = 0;
    this.maxRadius = 100 + Math.random() * 100;
    this.speed = 180 + Math.random() * 120;
    this.alive = true;
  }

  update(dt: number): void {
    this.radius += this.speed * dt;
    if (this.radius > this.maxRadius) this.alive = false;
  }

  draw(ctx: CanvasRenderingContext2D, theme: Theme): void {
    const alpha = 1 - this.radius / this.maxRadius;
    if (alpha <= 0) return;
    ctx.save();
    ctx.shadowColor = theme.primary;
    ctx.shadowBlur = 12;
    ctx.globalAlpha = alpha * 0.6;
    ctx.strokeStyle = theme.primary;
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2);
    ctx.stroke();
    ctx.globalAlpha = alpha * 0.3;
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.radius * 0.5, 0, Math.PI * 2);
    ctx.stroke();
    ctx.restore();
  }
}

// ========== Hook ==========
interface UseMatrixRainReturn {
  columnsRef: React.MutableRefObject<Column[]>;
  userColumnsRef: React.MutableRefObject<UserColumn[]>;
  ripplesRef: React.MutableRefObject<Ripple[]>;
  timeRef: React.MutableRefObject<number>;
  addRipple: (x: number, y: number) => void;
}

export function useMatrixRain(): UseMatrixRainReturn {
  const { state, modeConfig } = useMatrix();
  const columnsRef = useRef<Column[]>([]);
  const userColumnsRef = useRef<UserColumn[]>([]);
  const ripplesRef = useRef<Ripple[]>([]);
  const timeRef = useRef(0);

  const buildColumns = useCallback(() => {
    const config = modeConfig();
    const W = window.innerWidth;
    const spacing = state.fontSize * 1.1;
    const count = Math.ceil(W / spacing * state.density);
    const cols: Column[] = [];
    for (let i = 0; i < count; i++) {
      cols.push(new Column(i * spacing + spacing / 2, state.fontSize, config, state.charPools));
    }
    columnsRef.current = cols;
  }, [state.fontSize, state.charPools, state.density, modeConfig]);

  const buildUserColumns = useCallback((text: string) => {
    const W = window.innerWidth;
    const cols: UserColumn[] = [];
    try {
      clearCache();
      const font = '22px "PingFang SC","Microsoft YaHei",monospace';
      const prepared = prepareWithSegments(text, font);
      const result = layoutWithLines(prepared, W * 0.8, 30);
      for (let li = 0; li < result.lines.length; li++) {
        const chars = [...result.lines[li].text];
        const chunkSize = Math.max(1, Math.ceil(chars.length / Math.ceil(W / 100)));
        for (let c = 0; c < chars.length; c += chunkSize) {
          const chunk = chars.slice(c, c + chunkSize).join('');
          const x = (W / (chars.length + 1)) * (c + 1);
          cols.push(new UserColumn(chunk, x, c * 0.3 + li * 5, state.fontSize, state.charPools));
        }
      }
    } catch {
      // Fallback: simple per-character columns
      const chars = [...text];
      const step = Math.max(1, Math.ceil(chars.length / Math.ceil(W / 80)));
      for (let c = 0; c < chars.length; c += step) {
        const chunk = chars.slice(c, c + step).join('');
        const x = (W / (chars.length + 1)) * (c + 1);
        cols.push(new UserColumn(chunk, x, c * 0.3, state.fontSize, state.charPools));
      }
    }
    userColumnsRef.current = cols;
  }, [state.fontSize, state.charPools]);

  useEffect(() => {
    buildColumns();
  }, [buildColumns, state.mode]);

  useEffect(() => {
    buildUserColumns(state.userText);
  }, [buildUserColumns, state.userText]);

  useEffect(() => {
    const handleResize = () => {
      buildColumns();
      buildUserColumns(state.userText);
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [buildColumns, buildUserColumns, state.userText]);

  const addRipple = useCallback((x: number, y: number) => {
    ripplesRef.current.push(new Ripple(x, y));
    ripplesRef.current.push(new Ripple(x + (Math.random() - 0.5) * 20, y + (Math.random() - 0.5) * 20));
  }, []);

  return {
    columnsRef,
    userColumnsRef,
    ripplesRef,
    timeRef,
    addRipple,
  };
}

export { Column, UserColumn, Ripple };
