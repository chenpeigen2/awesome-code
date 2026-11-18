import { useState, useEffect } from 'react';
import { useMatrix } from '../contexts/MatrixContext';

const TITLES: string[] = [
  'WAKE UP NEO...',
  'THE MATRIX HAS YOU...',
  'FOLLOW THE WHITE RABBIT',
  'THERE IS NO SPOON',
  'FREE YOUR MIND',
];

const GLITCH_CHARS = 'アイウエオカキク0123456789!@#$%';

export default function TitleGlitch() {
  const { themeConfig } = useMatrix();
  const theme = themeConfig();
  const [text, setText] = useState('');
  const [visible, setVisible] = useState(true);
  const [idx, setIdx] = useState(0);

  // 切换标题时同步清空已打出的文字：用 React 官方的「渲染期调整 state」写法，
  // 而不是放在 effect 里同步 setState
  const [prevIdx, setPrevIdx] = useState(idx);
  if (idx !== prevIdx) {
    setPrevIdx(idx);
    setText('');
  }

  useEffect(() => {
    const target = TITLES[idx];
    let i = 0;
    const iv = setInterval(() => {
      i++;
      setText(target.slice(0, i));
      if (i >= target.length) {
        clearInterval(iv);
        setTimeout(() => {
          setVisible(false);
          setTimeout(() => {
            setIdx(prev => (prev + 1) % TITLES.length);
            setVisible(true);
          }, 1500);
        }, 3000);
      }
    }, 80);
    return () => clearInterval(iv);
  }, [idx]);

  // 故障效果依赖随机数，但随机必须在 effect 里产生：渲染期调用随机函数
  // 会让每次渲染输出不同，并发渲染下可能出现不一致
  const [glitchCells, setGlitchCells] = useState<{ char: string; transform: string }[]>([]);

  useEffect(() => {
    const rollGlitch = () => {
      setGlitchCells(
        text.split('').map(ch => ({
          char: Math.random() < 0.02
            ? GLITCH_CHARS[Math.floor(Math.random() * GLITCH_CHARS.length)]
            : ch,
          transform: Math.random() < 0.03
            ? `translateY(${(Math.random() - 0.5) * 8}px)`
            : 'none',
        })),
      );
    };

    rollGlitch();
    const iv = setInterval(rollGlitch, 100);
    return () => clearInterval(iv);
  }, [text]);

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      pointerEvents: 'none',
      zIndex: 4,
      transition: 'opacity 0.8s',
      opacity: visible ? 0.06 : 0,
    }}>
      <span style={{
        color: theme.primary,
        fontFamily: 'monospace',
        fontSize: 'clamp(40px, 8vw, 100px)',
        fontWeight: 'bold',
        letterSpacing: 6,
        textShadow: `0 0 40px ${theme.primary}40`,
        whiteSpace: 'nowrap',
      }}>
        {glitchCells.map((cell, i) => (
          <span key={i} style={{
            display: 'inline-block',
            transform: cell.transform,
          }}>
            {cell.char}
          </span>
        ))}
      </span>
    </div>
  );
}
