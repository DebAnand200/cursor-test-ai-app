import React, { useEffect, useMemo, useRef, useState } from 'react'
import { useAuthStore } from '../state/authStore'
import { api } from '../api/client'
import { DndProvider, useDrag, useDrop } from 'react-dnd'
import { HTML5Backend } from 'react-dnd-html5-backend'

type Board = { id: number; name: string }
type TaskItem = { id: number; title: string; description?: string; status: string; position: number }

const ItemTypes = { TASK: 'TASK' }

const TaskCard: React.FC<{ task: TaskItem }> = ({ task }) => {
  const ref = useRef<HTMLDivElement | null>(null)
  const [, drag] = useDrag(() => ({ type: ItemTypes.TASK, item: task }))
  drag(ref)
  return (
    <div ref={ref} style={{ padding: 8, border: '1px solid #ddd', borderRadius: 6, marginBottom: 8, background: 'white' }}>
      <strong>{task.title}</strong>
      {task.description && <div style={{ fontSize: 12, color: '#666' }}>{task.description}</div>}
    </div>
  )
}

const Column: React.FC<{ title: string; status: string; tasks: TaskItem[]; onDrop: (t: TaskItem) => void }> = ({ title, status, tasks, onDrop }) => {
  const ref = useRef<HTMLDivElement | null>(null)
  const [, drop] = useDrop(() => ({
    accept: ItemTypes.TASK,
    drop: (item: TaskItem) => onDrop(item),
  }))
  drop(ref)
  return (
    <div ref={ref} style={{ flex: 1, padding: 12, background: '#f7f7f7', borderRadius: 8 }}>
      <h3>{title}</h3>
      {tasks.filter(t => t.status === status).map(t => (
        <TaskCard key={t.id} task={t} />
      ))}
    </div>
  )
}

export const DashboardPage: React.FC = () => {
  const email = useAuthStore(s => s.email)
  const tier = useAuthStore(s => s.tier)
  const logout = useAuthStore(s => s.logout)
  const [boards, setBoards] = useState<Board[]>([])
  const [selectedBoard, setSelectedBoard] = useState<Board | null>(null)
  const [tasks, setTasks] = useState<TaskItem[]>([])

  useEffect(() => {
    const init = async () => {
      const bs = await api.get<Board[]>(/api/boards)
      setBoards(bs)
      if (bs.length > 0) {
        setSelectedBoard(bs[0])
      }
    }
    init()
  }, [])

  useEffect(() => {
    const loadTasks = async () => {
      const ts = await api.get<TaskItem[]>()
      setTasks(ts)
    }
    loadTasks()
  }, [selectedBoard])

  const onDropTo = (status: string) => async (task: TaskItem) => {
    await api.patch(, { status })
    setTasks(prev => prev.map(t => (t.id === task.id ? { ...t, status } : t)))
  }

  const columns = useMemo(() => ([
    { key: 'TODO', title: 'Todo' },
    { key: 'IN_PROGRESS', title: 'In Progress' },
    { key: 'DONE', title: 'Done' },
  ]), [])

  return (
    <div style={{ padding: 16 }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <strong>{email}</strong> · Tier: {tier}
        </div>
        <div>
          <button onClick={logout}>Logout</button>
        </div>
      </header>
      <hr />
      <div style={{ marginBottom: 16 }}>
        <select value={selectedBoard?.id ?? ''} onChange={(e) => {
          const id = Number(e.target.value)
          setSelectedBoard(boards.find(b => b.id === id) ?? null)
        }}>
          <option value= disabled>Select a board</option>
          {boards.map(b => (
            <option key={b.id} value={b.id}>{b.name}</option>
          ))}
        </select>
        <button style={{ marginLeft: 8 }} onClick={async () => {
          const name = prompt('Board name?')
          const b = await api.post<Board>('/api/boards', { name })
          setBoards(prev => [...prev, b])
          setSelectedBoard(b)
        }}>New Board</button>
        {selectedBoard && (
          <button style={{ marginLeft: 8 }} onClick={async () => {
            const title = prompt('Task title?')
            const t = await api.post<TaskItem>(, { title })
            setTasks(prev => [...prev, t])
          }}>New Task</button>
        )}
      </div>
      <DndProvider backend={HTML5Backend}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 12 }}>
          {columns.map(c => (
            <Column key={c.key} title={c.title} status={c.key} tasks={tasks} onDrop={onDropTo(c.key)} />
          ))}
        </div>
      </DndProvider>
    </div>
  )
}
