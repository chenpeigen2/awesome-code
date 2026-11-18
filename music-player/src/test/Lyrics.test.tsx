import { describe, it, expect, vi } from 'vitest'
import { render } from '@testing-library/react'
import Lyrics from '@/components/Lyrics'

vi.mock('@/stores', () => ({
  usePlayerStore: vi.fn((selector) => {
    const state = {
      currentTrack: null,
      currentTime: 0,
    }
    return typeof selector === 'function' ? selector(state) : state
  }),
}))

const LRC = ['[00:00.00]第一行', '[00:10.00]第二行', '[00:20.00]第三行'].join('\n')

function activeLines(container: HTMLElement): string[] {
  return Array.from(container.querySelectorAll('.lyrics-line.active')).map(el => el.textContent ?? '')
}

describe('Lyrics 组件', () => {
  it('渲染解析后的全部歌词行', () => {
    const { container } = render(<Lyrics lyricsContent={LRC} currentTime={0} />)
    const lines = container.querySelectorAll('.lyrics-line')
    expect(lines).toHaveLength(3)
    expect(lines[0].textContent).toBe('第一行')
    expect(lines[2].textContent).toBe('第三行')
  })

  it('高亮当前时间对应的那一行', () => {
    const { container } = render(<Lyrics lyricsContent={LRC} currentTime={12} />)
    expect(activeLines(container)).toEqual(['第二行'])
  })

  it('时间早于第一句歌词时不高亮任何行', () => {
    const { container } = render(<Lyrics lyricsContent={LRC} currentTime={-1} />)
    expect(activeLines(container)).toEqual([])
  })
})
