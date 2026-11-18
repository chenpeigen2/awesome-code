export function formatPrice(price: string): string {
  const p = parseFloat(price);
  if (p === 0) return "Free";
  if (p < 0.00001) return `$${(p * 1_000_000).toFixed(3)}/M`;
  if (p < 0.001) return `$${(p * 1_000).toFixed(3)}/K`;
  return `$${p.toFixed(6)}`;
}

export function formatContext(length: number): string {
  if (length >= 1_000_000) return `${(length / 1_000_000).toFixed(1)}M`;
  if (length >= 1_000) return `${(length / 1_000).toFixed(0)}K`;
  return `${length}`;
}

export function formatDate(timestamp: number): string {
  return new Date(timestamp * 1000).toLocaleDateString("zh-CN");
}

export function formatNumber(n: number): string {
  if (n >= 1_000_000_000) return `${(n / 1_000_000_000).toFixed(1)}B`;
  if (n >= 1_000_000) return `${(n / 1_000_000).toFixed(1)}M`;
  if (n >= 1_000) return `${(n / 1_000).toFixed(1)}K`;
  return `${n}`;
}
