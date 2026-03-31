import { useState, useEffect, useCallback } from 'react';
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

  useEffect(() => {
    const target = TITLES[idx];
    let i = 0;
    setText('');
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

  const getDisplayChar = useCallback((char: string): string => {
    if (Math.random() < 0.02) return GLITCH_CHARS[Math.floor(Math.random() * GLITCH_CHARS.length)];
    return char;
  }, []);

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
        {text.split('').map((ch, i) => (
          <span key={i} style={{
            display: 'inline-block',
            transform: Math.random() < 0.03 ? `translateY(${(Math.random() - 0.5) * 8}px)` : 'none',
          }}>
            {getDisplayChar(ch)}
          </span>
        ))}
      </span>
    </div>
  );
}
