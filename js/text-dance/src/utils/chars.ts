// Character pools for Matrix rain
const KATAKANA = 'アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン';
const DIGITS = '0123456789';
const SYMBOLS = '!@#$%^&*()_+-=[]{}|;:<>?/~';
const LATIN = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
const CHINESE = '天地玄黄宇宙洪荒日月盈昃辰宿列张寒来暑往秋收冬藏闰余成岁律吕调阳云腾致雨露结为霜';

export type PoolKey = 'katakana' | 'digits' | 'symbols' | 'latin' | 'chinese';

export const ALL_POOLS: Record<PoolKey, string> = {
  katakana: KATAKANA,
  digits: DIGITS,
  symbols: SYMBOLS,
  latin: LATIN,
  chinese: CHINESE,
};

export function randomChar(pools: PoolKey[] = ['katakana', 'digits', 'symbols', 'latin']): string {
  const fullPool = pools.map(k => ALL_POOLS[k]).join('');
  return fullPool[Math.floor(Math.random() * fullPool.length)];
}

export function randomInt(min: number, max: number): number {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}
